package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.entity.Employee;
import com.xu.Jerry.mapper.EmployeeMapper;
import com.xu.Jerry.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends
        ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
