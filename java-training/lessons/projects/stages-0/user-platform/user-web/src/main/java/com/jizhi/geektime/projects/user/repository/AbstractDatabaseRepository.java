package com.jizhi.geektime.projects.user.repository;

import com.jizhi.geektime.function.ThrowableFunction;
import com.jizhi.geektime.projects.user.transaction.TransactionalCallBack;
import com.jizhi.geektime.projects.user.utils.MD5;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.primitiveToWrapper;


/**
 * 抽象关系型数据库的创库类，实现了通用的一些方法
 *
 * @author jizhi7
 * @since 1.0
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
                File alreadyExecuteFile = new File(ddlPath + File.separator + ".ex");
                if (!alreadyExecuteFile.exists()) {
                    alreadyExecuteFile.createNewFile();
                }
                String content = IOUtils.toString(new FileInputStream(alreadyExecuteFile), "utf-8");
                List<String> fileMD5List = new ArrayList<>();
                if (content != null && !"".equals(content)) {
                    for (String str : content.split(";")) {
                        fileMD5List.add(str);
                    }
                }

                for (File file : DDL.listFiles()) {
                    if (file.isFile() && !file.getAbsolutePath().equals(alreadyExecuteFile.getAbsolutePath())) {
                        String md5 = MD5.getMD5(file);
                        if (!fileMD5List.contains(md5)) {
                            String sqls = IOUtils.toString(new FileInputStream(file), "utf-8");
                            String[] sql = sqls.split(";");
                            for (String ddlSql : sql) {
                                executeDDL(ddlSql);
                            }
                            fileMD5List.add(md5);
                        }
                    }
                }
                IOUtils.write(StringUtils.join(fileMD5List.toArray(), ";"), new FileOutputStream(alreadyExecuteFile), "utf-8");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Logger logger = Logger.getLogger(AbstractDatabaseRepository.class.getName());

    /**
     * 异常错误的通用处理，
     */
    protected static Consumer<Throwable> THROWABLE_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());


    protected AbstractDatabaseRepository() {

        //通过 ClassLoader 加载 java.sql.DriverManager -> static 模块 {}
//        DriverManager.setLogWriter(new PrintWriter(System.out));
//
       /* try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Driver driver = DriverManager.getDriver("jdbc:derby:/db/user-platform;create=true");
            Connection connection = driver.connect("jdbc:derby:/db/user-platform;create=true", new Properties());

            this.dbConnectionManager = new DBConnectionManager(connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/


    }

    protected Connection getConnection() {
        return TransactionalCallBack.connectionThreadLocal.get();
//        return dbConnectionManager.getConnection();
    }


    /**
     * 通用的dml查询语句执行方法
     *
     * @param sql              sql语句
     * @param resultSetHandler 结果集的处理方法
     * @param exceptionHandler 异常处理方法
     * @param args             sql参数
     * @param <T>              结果集处理后的对于实体类
     * @return 结果集处理后的返回数据
     */
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

    /**
     * 通用的dml更新语句执行方法
     *
     * @param sql  sql语句
     * @param args sql语句参数
     * @return 更新语句影响的行数
     */
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

    /**
     * 执行ddl语句通用方法
     *
     * @param sql sql语句
     * @return ddl执行是否成功了
     */
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

    /**
     * 准备 prepareStatement 的sql参数，反射调用 prepareStatment.setString(1,""); 方法
     *
     * @param preparedStatement 语句
     * @param args              参数
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
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