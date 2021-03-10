package com.jizhi.geektime.projects.user.repository;

import com.jizhi.geektime.function.ThrowableFunction;
import com.jizhi.geektime.projects.user.domain.User;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 基于数据库的用户存储实现
 * @author jizhi7
 * @since 1.0
 **/
public class DatabaseUserRepository extends AbstractDatabaseRepository implements UserRepository {


    public DatabaseUserRepository() {
        super();
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users(name,password,email,phoneNumber) VALUES (?,?,?,?)";
        int rowNum = executeUpdate(sql, user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
        return rowNum > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        String QUERY_ALL_USERS_DML_SQL =
                "SELECT id,name,password,email,phoneNumber FROM users";

        return executeQuery(QUERY_ALL_USERS_DML_SQL, hh, super.THROWABLE_HANDLER);
    }

    private ThrowableFunction<ResultSet, List<User>> hh =
            resultSet -> {
                BeanInfo userBean = Introspector.getBeanInfo(User.class, Object.class);
                List<User> users = new ArrayList<>();
                while (resultSet.next()) {
                    User user = new User();
                    for (PropertyDescriptor property : userBean.getPropertyDescriptors()) {
                        String fieldName = property.getName();
                        Class<?> fieldType = property.getPropertyType();
                        String columnName = getColumnNameByFiledName(fieldName);
                        String methodName = super.resultSetMethodMappings.get(fieldType);
                        Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                        Object columnVal = resultSetMethod.invoke(resultSet, columnName);
                        property.getWriteMethod().invoke(user, columnVal);
                    }
                    users.add(user);
                }
                return users;
            };



    private String getColumnNameByFiledName(String fieldName) {
        return fieldName;
    }

}
