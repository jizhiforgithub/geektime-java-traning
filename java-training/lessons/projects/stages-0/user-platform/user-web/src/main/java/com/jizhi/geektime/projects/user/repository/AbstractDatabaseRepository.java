package com.jizhi.geektime.projects.user.repository;

import com.jizhi.geektime.function.ThrowableFunction;
import com.jizhi.geektime.projects.user.sql.DBConnectionManager;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.primitiveToWrapper;


/**
 * 2021/3/2
 * jizhi7
 **/
public abstract class AbstractDatabaseRepository {

    /**
     * preparedStatement 类的方法映射
     */
    protected static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();

    /**
     * ResultSet 类的方法映射
     */
    protected static Map<Class, String> resultSetMethodMappings = new HashMap<>();

    private static final String DATA_SOURCE_JNDI_NAME = "java:comp/env/jdbc/UserPlatformDB";

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //

    }

    /**
     * 初始化数据库，创建相应的表结构
     */
    public void initDatabase() {
        try {
            String ddlPath = getClass().getResource("/META-INF/db/DDL").getPath();
            if (ddlPath != null && !"".equals(ddlPath)) {
                File DDL = new File(ddlPath);
                for (File file : DDL.listFiles()) {
                    if (file.isFile()) {
                        String sqls = IOUtils.toString(new FileInputStream(file), "utf-8");
                        String[] sql = sqls.split(";");
                        for (String ddlSql : sql) {
                            executeDDL(ddlSql);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Logger logger = Logger.getLogger(AbstractDatabaseRepository.class.getName());

    private DBConnectionManager dbConnectionManager;

    /**
     * 异常错误的通用处理，
     */
    protected static Consumer<Throwable> THROWABLE_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());


    protected AbstractDatabaseRepository() {
        //通过 ClassLoader 加载 java.sql.DriverManager -> static 模块 {}
//        DriverManager.setLogWriter(new PrintWriter(System.out));
//
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Driver driver = DriverManager.getDriver("jdbc:derby:/db/user-platform;create=true");
            Connection connection = driver.connect("jdbc:derby:/db/user-platform;create=true", new Properties());

            this.dbConnectionManager = new DBConnectionManager(connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() {
        return dbConnectionManager.getConnection();
    }

    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> resultSetHandler,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            prepareStatementSqlArgs(preparedStatement, args);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSetHandler.apply(resultSet);

        } catch (Throwable throwable) {
            exceptionHandler.accept(throwable);
        }
        return null;
    }

    protected int executeUpdate(String sql, Object... args) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            prepareStatementSqlArgs(preparedStatement, args);
            int row = preparedStatement.executeUpdate();
            return row;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return -1;
    }

    protected boolean executeDDL(String sql) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeUpdate() == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void prepareStatementSqlArgs(PreparedStatement preparedStatement, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class<?> argType = arg.getClass();
            Class<?> wrapperType = primitiveToWrapper(argType);

            if (wrapperType == null) {
                wrapperType = argType;
            }

            String methodName = preparedStatementMethodMappings.get(argType);
            Method method = PreparedStatement.class.getMethod(methodName, int.class, wrapperType);
            method.invoke(preparedStatement, i + 1, args[i]);
        }
    }

}