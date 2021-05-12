package com.jizhi.springboot.cache.service.impl;

import com.jizhi.springboot.cache.repository.dao.UserDao;
import com.jizhi.springboot.cache.repository.domain.User;
import com.jizhi.springboot.cache.service.IUserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserServiceImpl implements IUserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Collection<User> getUserAllList() {
        return userDao.selectAllUser();
    }


    /**
     *  该注解标注的方法，有可能会不执行，因为缓存中已经有了数据
     * 1. @Cacheable的几个属性详解：
     *      cacheNames/value：指定缓存组件的名字
     *      key：缓存数据使用的key,可以用它来指定。默认使用方法参数的值，一般不需要指定
     *      keyGenerator：作用和key一样，二选一
     *      cacheManager和cacheResolver作用相同：指定缓存管理器，二选一
     *      condition：指定符合条件才缓存，比如：condition="#id>3"
     *              也就是说传入的参数id>3才缓存数据
     *      unless：否定缓存，当unless为true时不缓存，可以获取方法结果进行判断
     *      sync：是否使用异步模式
     */
    //@Cacheable(cacheNames= "person")
    //@Cacheable(cacheNames= "person",key="#id",condition="#id>3")
    @Cacheable(cacheNames = "user", key = "{#id}")
    @Override
    public User getUserById(int id) {
        return userDao.selectUserById(id);
    }

    /**
     *  该注解标注的方法一定会得到执行，并执行后将执行结果放到缓存中
     * @CachePut:即调用方法，又更新缓存数据 修改了数据库中的数据，同时又更新了缓存
     * <p>
     * 运行时机：
     * 1.先调用目标方法
     * 2.将目标方法返回的结果缓存起来
     * <p>
     * 测试步骤：
     * 1.查询1号的个人信息
     * 2.以后查询还是之前的结果
     * 3.更新1号的个人信息
     * 4.查询一号员工返回的结果是什么？
     * 应该是更新后的员工
     * 但只更新了数据库，但没有更新缓存是什么原因？
     * 5.如何解决缓存和数据库同步更新？
     * 这样写：@CachePut(cacheNames = "person",key = "#person.id")
     * @CachePut(cacheNames = "person",key = "#result.id")
     */
    @CachePut(cacheNames = "user", key = "#result.id")
    @Override
    public User modifyUserAgeById(int id, int age) {
        return userDao.updateUserAgeById(id, age);
    }

    /**
     * @CacheEvict:清除缓存 1.key:指定要清除缓存中的某条数据
     * 2.allEntries=true:删除缓存中的所有数据
     * beforeInvocation=false:默认是在方法之后执行清除缓存
     * 3.beforeInvocation=true:现在是在方法执行之前执行清除缓存，
     * 作用是：只清除缓存、不删除数据库数据
     */
    //@CacheEvict(cacheNames = "person",key = "#id")
    @CacheEvict(cacheNames = "user", allEntries = true)
    @Override
    public boolean deleteUserBuId(int id) {
        return userDao.deleteUserById(id) > 0;
    }

    /**
     * @Caching是 @Cacheable、@CachePut、@CacheEvict注解的组合
     * 以下注解的含义：
     * 1.当使用指定名字查询数据库后，数据保存到缓存
     * 2.现在使用id、age就会直接查询缓存，而不是查询数据库
     */
    @Caching(
            cacheable = {@Cacheable(value = "user", key = "#name")},
            put = {@CachePut(value = "user", key = "#result.id"),
                    @CachePut(value = "user", key = "#result.age")
            }
    )
    public Collection<User> getUserByName(String name) {
        return userDao.getUserByName(name);
    }
}
