package connectTest;

import com.jacky.dao.EmployeeMapper;
import com.jacky.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class EmployeeTest {

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
    public void testSelectByID() {
        try (SqlSession session = sqlSessionFactory.openSession()){
            EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
            Employee employee = mapper.selectByID(1);
            log.debug("查询信息为{}", employee);
        }
    }
}
