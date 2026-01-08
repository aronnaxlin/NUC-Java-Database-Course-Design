package site.aronnax.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import site.aronnax.entity.Property;
import site.aronnax.util.DBUtil;

/**
 * 房产数据访问对象 (Data Access Object)
 * 提供对 properties 表的 CRUD 操作
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class PropertyDAO {

    /**
     * 插入新房产
     *
     * @param property 房产对象
     * @return 插入成功返回生成的主键ID，失败返回null
     */
    public Long insert(Property property) {
        String sql = "INSERT INTO properties (building_no, unit_no, room_no, area, p_status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, property.getBuildingNo());
            pstmt.setString(2, property.getUnitNo());
            pstmt.setString(3, property.getRoomNo());
            pstmt.setDouble(4, property.getArea());
            pstmt.setString(5, property.getpStatus());

            if (property.getUserId() != null) {
                pstmt.setLong(6, property.getUserId());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入房产失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 根据ID删除房产
     *
     * @param pId 房产ID
     * @return 删除成功返回true
     */
    public boolean deleteById(Long pId) {
        String sql = "DELETE FROM properties WHERE p_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, pId);

            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("删除房产失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 更新房产信息
     *
     * @param property 房产对象（需包含pId）
     * @return 更新成功返回true
     */
    public boolean update(Property property) {
        String sql = "UPDATE properties SET building_no=?, unit_no=?, room_no=?, area=?, p_status=?, user_id=? WHERE p_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, property.getBuildingNo());
            pstmt.setString(2, property.getUnitNo());
            pstmt.setString(3, property.getRoomNo());
            pstmt.setDouble(4, property.getArea());
            pstmt.setString(5, property.getpStatus());

            if (property.getUserId() != null) {
                pstmt.setLong(6, property.getUserId());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }

            pstmt.setLong(7, property.getpId());

            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("更新房产失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 根据ID查询房产
     *
     * @param pId 房产ID
     * @return Property对象，不存在则返回null
     */
    public Property findById(Long pId) {
        String sql = "SELECT * FROM properties WHERE p_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, pId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProperty(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询房产失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 查询所有房产
     *
     * @return 房产列表
     */
    public List<Property> findAll() {
        String sql = "SELECT * FROM properties";
        List<Property> properties = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                properties.add(mapResultSetToProperty(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有房产失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return properties;
    }

    /**
     * 根据用户ID查询房产列表
     *
     * @param userId 用户ID
     * @return 房产列表
     */
    public List<Property> findByUserId(Long userId) {
        String sql = "SELECT * FROM properties WHERE user_id = ?";
        List<Property> properties = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                properties.add(mapResultSetToProperty(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询用户房产失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return properties;
    }

    /**
     * Find property by room number
     *
     * @param buildingNo Building number
     * @param unitNo     Unit number
     * @param roomNo     Room number
     * @return Property object or null if not found
     */
    public Property findByRoomNo(String buildingNo, String unitNo, String roomNo) {
        String sql = "SELECT * FROM properties WHERE building_no = ? AND unit_no = ? AND room_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, buildingNo);
            pstmt.setString(2, unitNo);
            pstmt.setString(3, roomNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProperty(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询房产失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 将ResultSet映射为Property对象
     */
    private Property mapResultSetToProperty(ResultSet rs) throws SQLException {
        Property property = new Property();
        property.setpId(rs.getLong("p_id"));
        property.setBuildingNo(rs.getString("building_no"));
        property.setUnitNo(rs.getString("unit_no"));
        property.setRoomNo(rs.getString("room_no"));
        property.setArea(rs.getDouble("area"));
        property.setpStatus(rs.getString("p_status"));

        long userId = rs.getLong("user_id");
        if (!rs.wasNull()) {
            property.setUserId(userId);
        }

        return property;
    }

    /**
     * 关闭数据库资源
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.err.println("关闭资源失败: " + e.getMessage());
        }
    }
}
