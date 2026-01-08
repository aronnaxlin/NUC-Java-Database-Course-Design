package site.aronnax.service;

/**
 * Utility Card Service Interface
 * Provides business logic for utility card management
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public interface UtilityCardService {

    /**
     * Top up utility card
     * CRITICAL: Must check for arrears before allowing top-up
     *
     * @param cardId Card ID
     * @param amount Top-up amount
     * @return true if successful, false if blocked by arrears
     * @throws IllegalStateException if property has unpaid fees
     */
    boolean topUp(Long cardId, Double amount);

    /**
     * Get card balance
     *
     * @param cardId Card ID
     * @return Current balance
     */
    Double getCardBalance(Long cardId);
}
