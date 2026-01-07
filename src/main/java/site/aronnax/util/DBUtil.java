package site.aronnax.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 提供统一的数据库连接管理，采用单例模式确保配置统一加载
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class DBUtil {

    // 数据库连接配置参数
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    // 静态代码块：类加载时自动读取配置文件
    static {
        try {
            // 加载配置文件
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader()
                    .getResourceAsStream("db.properties");

            if (is == null) {
                throw new RuntimeException("无法找到数据库配置文件 db.properties");
            }

            props.load(is);

            // 读取配置项
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");

            // 加载 MySQL 驱动
            Class.forName(driver);

            System.out.println("[DBUtil] 数据库配置加载成功");
            System.out.println("[DBUtil] 连接地址: " + url);

        } catch (IOException e) {
            System.err.println("[DBUtil] 配置文件读取失败: " + e.getMessage());
            throw new RuntimeException("数据库配置初始化失败", e);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBUtil] MySQL 驱动加载失败: " + e.getMessage());
            throw new RuntimeException("数据库驱动加载失败", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection 数据库连接对象
     * @throws SQLException 连接失败时抛出异常
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        System.out.println("[DBUtil] 数据库连接成功");
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn 待关闭的连接对象
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("[DBUtil] 数据库连接已关闭");
            } catch (SQLException e) {
                System.err.println("[DBUtil] 关闭连接失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试数据库连接是否可用
     *
     * @return true 连接成功，false 连接失败
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBUtil] 连接测试失败: " + e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }
}
