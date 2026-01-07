package site.aronnax.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import site.aronnax.entity.UtilityCard;
import site.aronnax.util.DBUtil;

/**
 * 水电卡数据访问对象 (Data Access Object)
 * 提供对 utility_cards 表的 CRUD 操作
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class UtilityCardDAO {

    /**
     * 插入新水电卡
     *
     * @param card 水电卡对象
     * @return 插入成功返回生成的主键ID，失败返回null
     */
    public Long insert(UtilityCard card) {
        String sql = "INSERT INTO utility_cards (p_id, card_type, balance, last_topup) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, card.getpId());
            pstmt.setString(2, card.getCardType());
            pstmt.setDouble(3, card.getBalance());

            if (card.getLastTopup() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(card.getLastTopup()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("插入水电卡失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 根据ID删除水电卡
     *
     * @param cardId 水电卡ID
     * @return 删除成功返回true
     */
    public boolean deleteById(Long cardId) {
        String sql = "DELETE FROM utility_cards WHERE card_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cardId);

            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("删除水电卡失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 更新水电卡信息
     *
     * @param card 水电卡对象（需包含cardId）
     * @return 更新成功返回true
     */
    public boolean update(UtilityCard card) {
        String sql = "UPDATE utility_cards SET p_id=?, card_type=?, balance=?, last_topup=? WHERE card_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, card.getpId());
            pstmt.setString(2, card.getCardType());
            pstmt.setDouble(3, card.getBalance());

            if (card.getLastTopup() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(card.getLastTopup()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }

            pstmt.setLong(5, card.getCardId());

            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("更新水电卡失败: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 根据ID查询水电卡
     *
     * @param cardId 水电卡ID
     * @return UtilityCard对象，不存在则返回null
     */
    public UtilityCard findById(Long cardId) {
        String sql = "SELECT * FROM utility_cards WHERE card_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cardId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUtilityCard(rs);
            }
        } catch (SQLException e) {
            System.err.println("查询水电卡失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 查询所有水电卡
     *
     * @return 水电卡列表
     */
    public List<UtilityCard> findAll() {
        String sql = "SELECT * FROM utility_cards";
        List<UtilityCard> cards = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToUtilityCard(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有水电卡失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return cards;
    }

    /**
     * 根据房产ID查询水电卡列表
     *
     * @param pId 房产ID
     * @return 水电卡列表
     */
    public List<UtilityCard> findByPropertyId(Long pId) {
        String sql = "SELECT * FROM utility_cards WHERE p_id = ?";
        List<UtilityCard> cards = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, pId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToUtilityCard(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询房产水电卡失败: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return cards;
    }

    /**
     * 将ResultSet映射为UtilityCard对象
     */
    private UtilityCard mapResultSetToUtilityCard(ResultSet rs) throws SQLException {
        UtilityCard card = new UtilityCard();
        card.setCardId(rs.getLong("card_id"));
        card.setpId(rs.getLong("p_id"));
        card.setCardType(rs.getString("card_type"));
        card.setBalance(rs.getDouble("balance"));

        Timestamp lastTopup = rs.getTimestamp("last_topup");
        if (lastTopup != null) {
            card.setLastTopup(lastTopup.toLocalDateTime());
        }

        return card;
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
