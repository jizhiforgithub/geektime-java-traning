package com.jizhi.geektime.projects.user.sql;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    //public static final String DB_CONNECTION_MANAGER_NAME = DBConnectionManager.class.getName();

    //@Resource(name = "jdbc/UserPlatformDB")
   // private DataSource dataSource;

    private final Logger logger = Logger.getLogger(DBConnectionManager.class.getName());

    @Resource(name = "jdbc/UserPlatformDB")
    private DataSource dataSource;

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    public DBConnectionManager() {
    }

    public EntityManager getEntityManager() {
        logger.info("当前 EntityManager 实现类：" + entityManager.getClass().getName());
        return entityManager;
    }

    public Connection getConnection() {
       /* try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;*/
        // 依赖查找
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        if (connection != null) {
            logger.log(Level.INFO, "获取 JNDI 数据库连接成功！");
        }
        return connection;
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

    public static final String DROP_USERS_TABLE_DDL_SQL = "DROP TABLE users";

    public static final String CREATE_USERS_TABLE_DDL_SQL = "CREATE TABLE users(" +
            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
            "name VARCHAR(16) NOT NULL, " +
            "password VARCHAR(64) NOT NULL, " +
            "email VARCHAR(64) NOT NULL, " +
            "phoneNumber VARCHAR(64) NOT NULL" +
            ")";

    public static final String INSERT_USER_DML_SQL = "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
            "('A','******','a@gmail.com','1') , " +
            "('B','******','b@gmail.com','2') , " +
            "('C','******','c@gmail.com','3') , " +
            "('D','******','d@gmail.com','4') , " +
            "('E','******','e@gmail.com','5')";


}
