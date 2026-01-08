package site.aronnax.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import site.aronnax.dao.PropertyDAO;
import site.aronnax.dao.UserDAO;
import site.aronnax.entity.Property;
import site.aronnax.entity.User;
import site.aronnax.service.OwnerService;

/**
 * Owner Service Implementation
 * Implements owner management business logic
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class OwnerServiceImpl implements OwnerService {

    private final UserDAO userDAO;
    private final PropertyDAO propertyDAO;

    public OwnerServiceImpl() {
        this.userDAO = new UserDAO();
        this.propertyDAO = new PropertyDAO();
    }

    @Override
    public List<Map<String, Object>> searchOwners(String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();

        // Search users by name or phone
        List<User> users = userDAO.searchByKeyword(keyword);

        for (User user : users) {
            // Get properties owned by this user
            List<Property> properties = propertyDAO.findByUserId(user.getUserId());

            for (Property property : properties) {
                Map<String, Object> ownerInfo = new HashMap<>();
                ownerInfo.put("user_id", user.getUserId());
                ownerInfo.put("name", user.getName());
                ownerInfo.put("phone", user.getPhone());
                ownerInfo.put("gender", user.getGender());
                ownerInfo.put("property_id", property.getpId());
                ownerInfo.put("building_no", property.getBuildingNo());
                ownerInfo.put("unit_no", property.getUnitNo());
                ownerInfo.put("room_no", property.getRoomNo());
                ownerInfo.put("area", property.getArea());
                ownerInfo.put("status", property.getpStatus());

                results.add(ownerInfo);
            }
        }

        // Also search by property room number
        // Note: This requires parsing the keyword into building, unit, room
        // For simplicity, we'll keep it as is for now
        // You can enhance this later to support "A1-1-101" format searches

        return results;
    }

    @Override
    public Map<String, Object> getOwnerWithProperties(Long userId) {
        Map<String, Object> result = new HashMap<>();

        User owner = userDAO.findById(userId);
        if (owner != null) {
            result.put("user_id", owner.getUserId());
            result.put("user_name", owner.getUserName());
            result.put("name", owner.getName());
            result.put("phone", owner.getPhone());
            result.put("gender", owner.getGender());
            result.put("user_type", owner.getUserType());

            List<Property> properties = propertyDAO.findByUserId(userId);
            result.put("properties", properties);
        }

        return result;
    }

    @Override
    public boolean updatePropertyOwner(Long propertyId, Long newOwnerId) {
        Property property = propertyDAO.findById(propertyId);
        if (property == null) {
            return false;
        }

        // Verify new owner exists
        User newOwner = userDAO.findById(newOwnerId);
        if (newOwner == null) {
            return false;
        }

        property.setUserId(newOwnerId);
        return propertyDAO.update(property);
    }
}
