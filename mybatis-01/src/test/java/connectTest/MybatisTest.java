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
            User user = new User(0,"itnanls","");
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
            map.put("username1", "itnanls");
            map.put("password", "123456");
            UserMapper mapper1 = session.getMapper(UserMapper.class);
            User user1 = mapper1.selectByMap(map);
            log.debug(user1.toString());
        }
    }

    @Test
    public void TestSelectByUsername() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> i = mapper.selectByName("i");
            log.debug(i.toString());
        }
    }


    @Test
    public void TestSelectByIds() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<Integer> a = List.of(1,2,3,4,5);
            List<User> users = mapper.selectByIds(a);
            log.debug("select users are {}", users);
        }
    }

    @Test
    public void insertTest() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            try {
                mapper.insert(new User(1334, "lucy", "13131"));
                int a = 1/0;
                session.commit();

            } catch (Exception e) {
                log.debug("[{}] some thing wrong", e.toString());
                session.rollback();
            }

        }
    }

    @Test
    public void updateTest() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            try {
                int newlucy = mapper.updateByUserID(new User(134, "newlucy1", "131312"));
                log.debug("受影响的行数{}", newlucy);
            } catch (Exception e) {
                log.debug("[{}] some thing wrong", e.toString());
                session.rollback();
            }
        }
    }

    @Test
    public void deleteTest() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            try {
                int i = mapper.deleteByID(134);
                log.debug("受影响行数{}", i);
            } catch (Exception e) {
                log.debug("[{}] some thing wrong", e.toString());
                session.rollback();
            }
        }
    }


}
