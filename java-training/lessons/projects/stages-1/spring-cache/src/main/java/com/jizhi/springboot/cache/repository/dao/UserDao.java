package com.jizhi.springboot.cache.repository.dao;

import com.jizhi.springboot.cache.repository.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<User> selectAllUser() {
        System.out.println("======全部查询数据库=======");
        String sql = "select id,name,age from user ";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User selectUserById(int id) {
        System.out.println("======单个查询数据库=======");
        String sql = "select id,name,age from user where id = ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
    }

    public User updateUserAgeById(int id, int age) {
        System.out.println("======单个更新、查询数据库=======");
        String sql = "update user set age=? where id = ?";
        jdbcTemplate.update(sql, age, id);
        return selectUserById(id);
    }

    public int deleteUserById(int id) {
        System.out.println("======单个删除数据库=======");
        String sql = "delete user where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public Collection<User> getUserByName(String name) {
        System.out.println("======根据名字查询数据库=======");
        String sql = "select id,name,age from user where name =?";
        return jdbcTemplate.query(sql, new UserRowMapper(), name);
    }
}
