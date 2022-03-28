## 3.28 mybatis记录
1. 利用Maven管理项目， 首先建立一个maven项目然后删除其src文件夹，仅仅作为管理vision的项目
2. 引入相关的依赖
3. Mybatis做好了很多的封装，我们仅需要进行简单的配置即可进行数据库的查询
首先创建一个实体对象，用来与数据库中的对象进行对应
例如`com.jacky.entity`下的 User 对象
```java
package com.jacky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public int id;
    public String username;
    public String password;
}

```
构建一个Bean对象，利用lombok进行简化开发。

其次在`src/main/resources`下进行相关的文件配置
首先是关于数据库的相关配置，其代码入下
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties>
        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="your JDBC connect URL"/>
        <property name="username" value="Your username"/>
        <property name="password" value="Your Password"/>
    </properties>

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="UserMapper.xml"/>
    </mappers>
</configuration>
```
然后根据需要查询的对象，建立一个dao包，dao为持久化操作
与之前学习JDBC不相同的是，利用 Mybatis 我们无需显示的写出JDBC的代码即可完成数据的相关操作

首先定义接口 `UserMapper` 在其中完成对数据库的相关操作的声明。

```java
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
```
在接口中，只需要定义方法，该函数签名指定了传入参数与返回类型，并用`@Param`注解指定参数的别名
是的在稍后的Mapper文件中可以准确的传入参数。
简单来说 dao 层的接口指定了一种规范即 需要传入的参数 和 从数据库中根据这些参数查询出来的结果的
类型。

声明了接口中的方法之后，需要编写Mapper.xml，在此文件中我们完成数据库的具体操作
与上面 `UserMapper` 对应的XML文件如下。
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.jacky.dao.UserMapper">
    <select id="selectALL" resultType="com.jacky.entity.User">
        select * from user
    </select>

    <select id="selectOne" resultType="com.jacky.entity.User">
        select * from user where id = #{id}
    </select>

    <select id="select" resultType="com.jacky.entity.User">
        select * from user where username = #{username} and password = #{password}
    </select>

    <select id="selectByUser" resultType="com.jacky.entity.User">
        select * from user where username = #{username} and password = #{password}
    </select>

    <select id="selectByMap" resultType="com.jacky.entity.User">
        select * from user where username = #{username} and password = #{password}
    </select>
</mapper>
```
以`select`标签为例，其 id 与接口中的函数名相同，指定此标签完成那个函数的功能
`resultType`指定对于哪个对象进行查询以便在框架中完成查询之后的自动转换。
参数的传递则利用模板 `#{name}` 进行传递
在接口中利用 `@Param("name")` 进行传递，在标签中便可直接根据 `name` 获取。
值得注意的是：
```markdown
当传入参数为对象，如User对象， 或者Map的话，框架会自动匹配 User 的字段，或者自动
获取Map的Key进行匹配
如传入 User 后利用 User.username 进行匹配 可以直接写成 #{username},
Map 同理
```

当完成相关配置之后进行代码的开发,具体代码如下
```java
package connectTest;

import com.jacky.dao.UserMapper;
import com.jacky.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class MybatisTest {
    public String resource;
    public InputStream inputStream;
    public SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() {
        resource = "mybatis-config.xml";
        try {
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            log.debug("获取 [mybatis] 配置文件出错 "+e);
        }

    }

    @Test
    public void ConnectionTest() throws IOException {

//        try (SqlSession session = sqlSessionFactory.openSession()) {
//            Blog blog = (Blog) session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
//        }
        log.debug("sqlSessionfactory is [{}]", sqlSessionFactory);
        try (SqlSession session = sqlSessionFactory.openSession()) {
            List<Object> objects = session.selectList("com.jacky.dao.UserMapper.selectAll");
            for (Object object : objects) {
                log.debug("result is [{}]", object);
            }
        }
    }

    @Test
    public void Test2() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectALL();
            System.out.println(users);
        }
    }

    @Test
    public void TestFindById() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectOne(1);
            log.debug(user.toString());
        }
    }

    @Test
    public void TestSelect() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.select("itnanls", "123456");
            log.warn(user.toString());
        }
    }

    @Test
    public void TestSelectByUSer() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = new User(2,"itnanls","123456");
            UserMapper mapper1 = session.getMapper(UserMapper.class);
            User user1 = mapper1.selectByUser(user);
            log.debug(user1.toString());
        }
    }

    @Test
    public void TestSelectByMap() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            Map<String, Object> map = new HashMap<>();
            map.put("username", "itnanls");
            map.put("password", "123456");
            UserMapper mapper1 = session.getMapper(UserMapper.class);
            User user1 = mapper1.selectByMap(map);
            log.debug(user1.toString());
        }
    }
}

```

首先是在 `@Before` 的注解函数中进行注册，并从sqlSessionFactory中拿到一个 session，
当Mapper已经写好之后直接调用
```java
UserMapper mapper = session.getMapper(UserMapper.class);
User user = mapper.select("itnanls", "123456");
```
`session.getMapper(A.class)` 为获取接口A的对象，
直接调用其中声明好的方法，Mybatis框架会自动去相应的Mappper.xml文件中找寻相应的
数据库实现，并且将相关对象包装好并且返回。