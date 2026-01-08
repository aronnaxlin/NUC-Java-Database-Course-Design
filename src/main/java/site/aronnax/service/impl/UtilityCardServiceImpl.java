package site.aronnax.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.aronnax.dao.UtilityCardDAO;
import site.aronnax.entity.UtilityCard;
import site.aronnax.service.FeeService;
import site.aronnax.service.UtilityCardService;

/**
 * Utility Card Service Implementation
 * Implements utility card management with arrears checking
 *
 * @author Aronnax (Li Linhan)
 */
@Service
@RequiredArgsConstructor
public class UtilityCardServiceImpl implements UtilityCardService {

    private final UtilityCardDAO utilityCardDAO;
    private final FeeService feeService;

    @Override
    public boolean topUp(Long cardId, Double amount) {
        // Get card information
        UtilityCard card = utilityCardDAO.findById(cardId);
        if (card == null) {
            throw new RuntimeException("水电卡不存在");
        }

        // CRITICAL: Check for wallet arrears before allowing top-up
        boolean hasArrears = feeService.checkWalletArrears(card.getpId());
        if (hasArrears) {
            throw new IllegalStateException("您有未缴的物业费/取暖费，请先缴清欠款后再充值");
        }

        // Proceed with top-up
        Double currentBalance = card.getBalance() != null ? card.getBalance() : 0.0;
        card.setBalance(currentBalance + amount);
        card.setLastTopup(LocalDateTime.now());

        return utilityCardDAO.update(card);
    }

    @Override
    public Double getCardBalance(Long cardId) {
        UtilityCard card = utilityCardDAO.findById(cardId);
        return card != null ? card.getBalance() : null;
    }
}
