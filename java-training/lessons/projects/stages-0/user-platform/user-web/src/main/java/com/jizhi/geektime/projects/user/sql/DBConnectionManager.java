package com.jizhi.geektime.projects.user.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 2021/3/2
 * jizhi7
 **/
public class DBConnectionManager {

    private Connection connection;

    public DBConnectionManager(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void releaseConnection() {
        if(this.connection != null) {
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
