package com.jizhi.geektime.projects.user.sql;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接管理工具
 *
 * @author jizhi7
 * @since 1.0
 **/
public class DBConnectionManager {

    /**
     * 获取DBConnectionManager实例
     *
     * @return
     */
    /*public static DBConnectionManager getInstance() {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl != null) {
            ServletContext sc = IoCContainer.currentContextPerThread.get(ccl);
            if (sc != null) {
                return (DBConnectionManager) sc.getAttribute(DB_CONNECTION_MANAGER_NAME);
            }
        }
        return null;
    }*/

    public static final String DB_CONNECTION_MANAGER_NAME = DBConnectionManager.class.getName();

    @Resource(name = "jdbc/UserPlatformDB")
    private DataSource dataSource;

    public DBConnectionManager() {
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
