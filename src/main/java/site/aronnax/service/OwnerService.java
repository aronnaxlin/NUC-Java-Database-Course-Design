package site.aronnax.service;

import java.util.List;
import java.util.Map;

/**
 * Owner Service Interface
 * Provides business logic for owner management
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public interface OwnerService {

    /**
     * Multi-dimensional search for owners
     * Search by name, phone, or room number
     *
     * @param keyword Search keyword
     * @return List of matching owner details (includes properties)
     */
    List<Map<String, Object>> searchOwners(String keyword);

    /**
     * Get owner with all their properties
     *
     * @param userId User ID
     * @return Map containing owner info and properties
     */
    Map<String, Object> getOwnerWithProperties(Long userId);

    /**
     * Update property owner (change ownership)
     *
     * @param propertyId Property ID
     * @param newOwnerId New owner user ID
     * @return true if successful
     */
    boolean updatePropertyOwner(Long propertyId, Long newOwnerId);
}
