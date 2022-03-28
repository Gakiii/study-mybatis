package com.jacky.dao;

import com.jacky.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    /**
     * 查询所有用户
     */
    List<User> selectALL();

    /**
     * 根据id查询用户
     */
    User selectOne(int id);

    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    User select(@Param("username") String username, @Param("password") String password);

    /**
     * 根据用户对象用户
     * @return
     */
    User selectByUser(User user);


    /**
     * 根据用户对象用户
     * @return
     */
    User selectByMap(Map<String, Object> map);
}
