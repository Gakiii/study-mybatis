package com.jacky.dao;

import com.jacky.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeMapper {
    Employee selectByID(@Param("id") Integer id);
}
