package site.aronnax.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.aronnax.dao.FeeDAO;
import site.aronnax.dao.PropertyDAO;
import site.aronnax.dao.UserWalletDAO;
import site.aronnax.dao.UtilityCardDAO;
import site.aronnax.dao.WalletTransactionDAO;
import site.aronnax.entity.Fee;
import site.aronnax.entity.Property;
import site.aronnax.entity.UserWallet;
import site.aronnax.entity.UtilityCard;
import site.aronnax.entity.WalletTransaction;
import site.aronnax.service.WalletService;

/**
 * Wallet Service Implementation
 * Implements wallet management business logic with critical arrears checking
 *
 * @author Aronnax (Li Linhan)
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserWalletDAO walletDAO;
    private final WalletTransactionDAO transactionDAO;
    private final FeeDAO feeDAO;
    private final UtilityCardDAO cardDAO;
    private final PropertyDAO propertyDAO;

    @Override
    public boolean rechargeWallet(Long userId, Double amount) {
        if (amount <= 0)
            return false;

        // Get or create wallet
        UserWallet wallet = walletDAO.findByUserId(userId);
        if (wallet == null) {
            if (!createWallet(userId))
                return false;
            wallet = walletDAO.findByUserId(userId);
        }

        // Update balance
        Double currentBalance = wallet.getBalance() != null ? wallet.getBalance() : 0.0;
        Double newBalance = currentBalance + amount;
        wallet.setBalance(newBalance);

        // Update total recharged
        Double totalRecharged = wallet.getTotalRecharged() != null ? wallet.getTotalRecharged() : 0.0;
        wallet.setTotalRecharged(totalRecharged + amount);

        boolean success = walletDAO.update(wallet);

        if (success) {
            recordTransaction(wallet.getWalletId(), "RECHARGE", amount, newBalance, null, "钱包充值: +" + amount + "元");
        }

        return success;
    }

    @Override
    public boolean payFeeFromWallet(Long feeId) {
        Fee fee = feeDAO.findById(feeId);
        if (fee == null || fee.getIsPaid() == 1 || !"WALLET".equals(fee.getPaymentMethod())) {
            return false;
        }

        Property property = propertyDAO.findById(fee.getpId());
        if (property == null || property.getUserId() == null) {
            return false;
        }

        Long userId = property.getUserId();
        UserWallet wallet = walletDAO.findByUserId(userId);
        if (wallet == null)
            return false;

        Double currentBalance = wallet.getBalance() != null ? wallet.getBalance() : 0.0;
        if (currentBalance < fee.getAmount())
            return false;

        Double newBalance = currentBalance - fee.getAmount();
        wallet.setBalance(newBalance);

        boolean walletUpdated = walletDAO.update(wallet);
        if (!walletUpdated)
            return false;

        fee.setIsPaid(1);
        fee.setPayDate(LocalDateTime.now());
        boolean feeUpdated = feeDAO.update(fee);

        if (feeUpdated) {
            recordTransaction(wallet.getWalletId(), "PAY_FEE", -fee.getAmount(), newBalance, feeId,
                    "缴费: " + fee.getFeeType() + " -" + fee.getAmount() + "元");
        }

        return feeUpdated;
    }

    @Override
    public boolean topUpCardFromWallet(Long userId, Long cardId, Double amount) {
        if (amount <= 0)
            return false;

        if (checkWalletArrears(userId)) {
            throw new IllegalStateException("您有未缴的物业费/取暖费，请先缴清欠款后再充值");
        }

        UserWallet wallet = walletDAO.findByUserId(userId);
        if (wallet == null)
            return false;

        Double currentWalletBalance = wallet.getBalance() != null ? wallet.getBalance() : 0.0;
        if (currentWalletBalance < amount)
            return false;

        UtilityCard card = cardDAO.findById(cardId);
        if (card == null)
            return false;

        Property property = propertyDAO.findById(card.getpId());
        if (property == null || !property.getUserId().equals(userId))
            return false;

        Double newWalletBalance = currentWalletBalance - amount;
        wallet.setBalance(newWalletBalance);
        if (!walletDAO.update(wallet))
            return false;

        Double currentCardBalance = card.getBalance() != null ? card.getBalance() : 0.0;
        card.setBalance(currentCardBalance + amount);
        card.setLastTopup(LocalDateTime.now());
        boolean cardUpdated = cardDAO.update(card);

        if (cardUpdated) {
            recordTransaction(wallet.getWalletId(), "TOPUP_CARD", -amount, newWalletBalance, cardId,
                    "充值" + card.getCardType() + "卡: -" + amount + "元");
        }

        return cardUpdated;
    }

    @Override
    public boolean checkWalletArrears(Long userId) {
        List<Property> properties = propertyDAO.findByUserId(userId);
        for (Property property : properties) {
            List<Fee> fees = feeDAO.findByPropertyId(property.getpId());
            for (Fee fee : fees) {
                if (fee.getIsPaid() == 0 && "WALLET".equals(fee.getPaymentMethod())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Double getWalletBalance(Long userId) {
        UserWallet wallet = walletDAO.findByUserId(userId);
        return wallet != null ? wallet.getBalance() : null;
    }

    @Override
    public List<WalletTransaction> getTransactionHistory(Long userId) {
        UserWallet wallet = walletDAO.findByUserId(userId);
        if (wallet == null)
            return List.of();
        return transactionDAO.findByWalletId(wallet.getWalletId());
    }

    @Override
    public boolean createWallet(Long userId) {
        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setBalance(0.0);
        wallet.setTotalRecharged(0.0);
        return walletDAO.insert(wallet) != null;
    }

    private void recordTransaction(Long walletId, String transType, Double amount, Double balanceAfter, Long relatedId,
            String description) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(walletId);
        transaction.setTransType(transType);
        transaction.setAmount(Math.abs(amount));
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRelatedId(relatedId);
        transaction.setDescription(description);
        transactionDAO.insert(transaction);
    }
}
