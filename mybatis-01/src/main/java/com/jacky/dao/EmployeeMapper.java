package com.jacky.dao;

import com.jacky.entity.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeMapper {
    Employee selectByID(@Param("id") Integer id);

    /**
     * 利用子查询的方式进行查询所有员工
     * @return
     */
   List<Employee> selectALL();

    /**
     * 利用join的方式进行查询所有员工
     * @return
     */
   List<Employee> selectALLJoin();
}
