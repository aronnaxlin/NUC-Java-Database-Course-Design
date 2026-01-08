package site.aronnax.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.aronnax.common.Result;
import site.aronnax.entity.WalletTransaction;
import site.aronnax.service.WalletService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/info")
    public Result<Double> getWalletBalance(@RequestParam("userId") Long userId) {
        Double balance = walletService.getWalletBalance(userId);
        if (balance == null) {
            // Attempt to create if null/not found? Or just return 0.
            // Service returns null if not found. Client expects value.
            // Let's return 0.0 or error.
            return Result.success(0.0);
        }
        return Result.success(balance);
    }

    @PostMapping("/recharge")
    public Result<String> recharge(@RequestParam("userId") Long userId, @RequestParam("amount") Double amount) {
        boolean success = walletService.rechargeWallet(userId, amount);
        if (success) {
            return Result.success("钱包充值成功");
        }
        return Result.error("充值失败");
    }

    @GetMapping("/transactions")
    public Result<List<WalletTransaction>> getTransactions(@RequestParam("userId") Long userId) {
        return Result.success(walletService.getTransactionHistory(userId));
    }

    @PostMapping("/pay-fee")
    public Result<String> payFee(@RequestParam("feeId") Long feeId) {
        boolean success = walletService.payFeeFromWallet(feeId);
        if (success) {
            return Result.success("缴费成功");
        }
        return Result.error("缴费失败: 余额不足或账单无效");
    }
}
