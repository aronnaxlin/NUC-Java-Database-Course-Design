package site.aronnax.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.aronnax.common.Result;
import site.aronnax.service.UtilityCardService;
import site.aronnax.service.WalletService;

@RestController
@RequestMapping("/api/utility")
public class UtilityCardController {

    private final UtilityCardService utilityCardService;
    private final WalletService walletService;

    public UtilityCardController(UtilityCardService utilityCardService, WalletService walletService) {
        this.utilityCardService = utilityCardService;
        this.walletService = walletService;
    }

    @GetMapping("/card/{id}")
    public Result<Double> getCardBalance(@PathVariable("id") Long id) {
        Double balance = utilityCardService.getCardBalance(id);
        if (balance == null) {
            return Result.error("卡片不存在");
        }
        return Result.success(balance);
    }

    @PostMapping("/card/topup")
    public Result<String> topUp(@RequestParam("cardId") Long cardId, @RequestParam("amount") Double amount) {
        try {
            boolean success = utilityCardService.topUp(cardId, amount);
            if (success) {
                return Result.success("充值成功");
            } else {
                return Result.error("充值失败");
            }
        } catch (IllegalStateException e) {
            // Critical Interception caught here
            return Result.error(e.getMessage()); // Will return error message about arrears
        } catch (Exception e) {
            return Result.error("系统错误: " + e.getMessage());
        }
    }

    // Additional endpoint to top up from wallet if needed, separating concerns
    @PostMapping("/card/topup-wallet")
    public Result<String> topUpFromWallet(@RequestParam("userId") Long userId, @RequestParam("cardId") Long cardId,
            @RequestParam("amount") Double amount) {
        try {
            boolean success = walletService.topUpCardFromWallet(userId, cardId, amount);
            if (success) {
                return Result.success("钱包转账充值成功");
            } else {
                return Result.error("充值失败，请检查余额或卡片状态");
            }
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }
}
