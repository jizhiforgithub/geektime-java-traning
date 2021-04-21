package com.jizhi.geektime.projects.user.repository;

import com.jizhi.geektime.projects.user.domain.User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存型 {@link UserRepository} 实现
 **/
public class InMemoryUserRepository implements UserRepository {

    private Map<Long, User> repository = new ConcurrentHashMap<>();

    @Override
    public boolean save(User user) {
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public boolean deleteById(Long userId) {
        return repository.remove(userId) != null;
    }

    @Override
    public boolean update(User user) {
        save(user);
        return true;
    }

    @Override
    public User getById(Long userId) {
        return repository.get(userId);
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return repository.values()
                .stream()
                .filter(user -> Objects.equals(userName, user.getName())
                        && Objects.equals(password, user.getPassword()))
                .findFirst()
                .get();
    }

    @Override
    public Collection<User> getAll() {
        return repository.values();
    }
}
