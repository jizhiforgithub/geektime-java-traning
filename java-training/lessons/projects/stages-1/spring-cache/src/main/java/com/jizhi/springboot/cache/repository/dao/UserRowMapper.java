package com.jizhi.springboot.cache.repository.dao;

import com.jizhi.springboot.cache.repository.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setName(rs.getString("name"));
        user.setId(rs.getInt("id"));
        user.setAge(rs.getInt("age"));
        return user;
    }
}
