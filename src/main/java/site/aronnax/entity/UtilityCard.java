package site.aronnax.entity;

import java.time.LocalDateTime;

/**
 * 水电卡实体类
 * 对应数据库 utility_cards 表
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class UtilityCard {

    private Long cardId; // 主键ID
    private Long pId; // 关联房产ID
    private String cardType; // 卡片类型: WATER, ELECTRICITY
    private Double balance; // 卡内余额
    private LocalDateTime lastTopup; // 最后充值时间

    // 无参构造器
    public UtilityCard() {
    }

    // 全参构造器
    public UtilityCard(Long cardId, Long pId, String cardType,
            Double balance, LocalDateTime lastTopup) {
        this.cardId = cardId;
        this.pId = pId;
        this.cardType = cardType;
        this.balance = balance;
        this.lastTopup = lastTopup;
    }

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public LocalDateTime getLastTopup() {
        return lastTopup;
    }

    public void setLastTopup(LocalDateTime lastTopup) {
        this.lastTopup = lastTopup;
    }

    @Override
    public String toString() {
        return "UtilityCard{" +
                "cardId=" + cardId +
                ", pId=" + pId +
                ", cardType='" + cardType + '\'' +
                ", balance=" + balance +
                ", lastTopup=" + lastTopup +
                '}';
    }
}
