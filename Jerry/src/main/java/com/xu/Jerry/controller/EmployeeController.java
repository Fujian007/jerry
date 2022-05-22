package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.Jerry.common.R;
import com.xu.Jerry.entity.Employee;
import com.xu.Jerry.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;




@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //登录
    @PostMapping("/login")
    public R<Employee> logIn(HttpServletRequest request, @RequestBody Employee e){
        String username = e.getUsername();
        byte[] pwd = e.getPassword().getBytes(StandardCharsets.UTF_8);
        String password = DigestUtils.md5DigestAsHex(pwd);
        //根据用户名和密码进行等值查询
        //mybatis-plus中有eq()方法进行等值查询
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,username);
        Employee login = employeeService.getOne(lqw);
        //账号为空
        if (login == null) {
            return R.error("用户名不存在!");
        }
        //账号状态不为1就是禁用
        if (login.getStatus() != 1){
            return R.error("该用户已被禁用!");
        }
        //密码不正确
        if (!password.equals(login.getPassword())){
            return R.error("密码不正确!");
        }
        //登录成功把账号放入到session中
        request.getSession().setAttribute("employee",login.getId());//储存到session中
        //System.out.println("controller的session中有：" + request.getSession().getAttribute());
        return R.success(login);
    }


    //退出登录
    @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request){
        //清除session中用户id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //添加员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工==> {}" + employee.toString());

        //设置初始密码123456，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置添加时间
        //employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //设置添加人的id
        //employee.setCreateUser(empId);
        //设置更新人的id
        //employee.setUpdateUser(empId);

        //保存到数据库
        boolean save = employeeService.save(employee);
        if (save){
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }



    /** 分页查询employee
     *
     * @param page 前端传过来的参数，当前页
     * @param pageSize 每页显示多少条内容
     * @param name  条件查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //如果name不为null，就加入like条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序时间
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    //修改员工的状态
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        //设置更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //设置更新人
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        boolean b = employeeService.updateById(employee);
        if (b){
            return R.success("员工信息修改成功!");
        }else {
            return R.error("员工信息修改失败!");
        }
    }



    //员工回显数据
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工: {}",id);
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }else {
            return R.error("没有查询到对应员工信息");
        }
    }
}
