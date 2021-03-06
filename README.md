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

## 3.29日学习报告
### "#" 和 "$" 的区别
"#" 是和预编译一样的填充机制，能够有效地避免sql注入的情况
"$" 只是单纯的字符串替换， 会导致sql注入等安全问题
推荐使用 "#{param}" 的方式
### 事务
默认seeion的事务的自动提交关闭的，可以在参数中加入true来开启自动提交
也可以进行手动管理
### 注解开发
不常用 详见ydlclass的课程

### 别名设置
在开发过程中有可能存在数据库字段和Bean中的实体类的字段不相匹配的情况
这样会导致Mybatis的自动将结果转换为Bean类出错，这个时候可以设置一个映射结果
在UserMapper.xml中进行如下配置
```xml
<resultMap id="userMap" type="com.jacky.entity.User">
    <id column="id" property="id"/>
<!--    前面为数据库名称  后面为配置文件中的名称-->
    <result column="user_name" property="username"/>
    <result column="password" property="password"/>
</resultMap>
```
同时我们在指定mybatis的转化类型，即resultType的时候，使用全限定名称会稍许麻烦(使用IDEA插件
MybatisX 可以自动生成)
可以设置别名, 在mybatis-config.xml中进行如下设置
```xml
    <typeAliases>
        <typeAlias type="com.jacky.entity.User"  alias="user"/>
    </typeAliases>
```
之后进行就可以利用user代替了(大小写不重要，mybatis会自动帮我我们处理)

### 动态SQL (重要！)
动态SQL的使用使得SQL非常灵活
主要可以使用Mybatis的标签，<if></if> 进行判断，如利用if标签进行查询的代码可以如下
```xml
    <select id="selectByUser" resultMap="userMap">
        select id,user_name,password from user
        <where>
            <if test="id != 0">
                and id = #{id}
            </if>
            <if test="username != null and username != ''">
                and username=#{username}
            </if>
            <if test="password != null and password != ''">
                and password=#{password}
            </if>
        </where>
    </select>
```
添加where后可以解决and的重复问题

进行更新的时候可以使用<set>标签，其代码如下
```xml
<update id="updateByUserID">
        update user
        <set>
            <if test="username != null and username !=''">
                user_name = #{username},
            </if>
            <if test="password != null and username != ''">
                password = #{password},
            </if>
        </set>
        <where>
            <if test="id != 0">
                id = #{id}
            </if>
        </where>
    </update>
```

### 高级映射
在数据库中，对于一个员工，我们一般存储的是其部门ID，但是在Java实体类中
我们会将整个部门信息作为一个对象来存储。这个时候Mybatis无法自动完成从数据库
的ID，映射为一个Java对象的过程，需要我们手动的进行一些配置才能完成转换。

道理很简单，我们如果仅从数据库中查询到部门ID的话，部门对象的字段并不完整，不能将其封装成一个部门对象
首先就应该将所有的数据进行查询出来。在SQL中有子查询和联合查询两者方式，对应这Mybatis中的两种映射方式

首先是子查询的方式，（这种方式较为复杂，且时间复杂度高）

当我们查询到某个员工的时候，根据其查询到的did(部门ID)，再去查询部门信息
对应的两条查询语句在各自的mapper.xml中如下:
根据did 查询部门信息
```xml
<select id="select" resultType="com.jacky.entity.Dept">
        select id, name from dept
        <where>
            id = #{id}
        </where>
</select>
```
查询员工信息, 注意 这里是将结果转换成了一个resultMap
```xml
<select id="selectALL" resultMap="selectEmployee">     
    select id, `name`, did from employee
</select>
```
而resultMap如下：
```xml
<resultMap id="selectEmployee" type="com.jacky.entity.Employee">
<!--    常规封装-->
    <id column="id" property="id"/>
    <result column="name" property="name"/>
    
<!--    将colum(did) 作为参数 用 (select) 语句查询 结果映射为 javaType-->
<!--    对应着为java对象中的字段 (property 为 dept)-->
    <association property="dept" column="did" javaType="com.jacky.entity.Dept" select="com.jacky.dao.DeptMapper.select">
        <id property="id" column="id"></id>
        <result property="name" column="name"></result>
    </association>
</resultMap>
```

接下来是联合查询，有了上述的基础，联合查询就易懂很多
查询以及映射的方式如下
```xml
<resultMap id="selectAllEmployByJoin" type="com.jacky.entity.Employee">
        <id column="eid" property="id"/>
        <result column="ename" property="name"/>
        <association property="dept" javaType="com.jacky.entity.Dept">
            <id column="did" property="id"/>
            <result column="dname" property="name"/>
        </association>
</resultMap>

<select id="selectALLJoin" resultType="com.jacky.entity.Employee">
    select e.id, e.name, d.id, d.name from employee e left join dept d on d.id = e.did
</select>
```




