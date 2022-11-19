package com.demo.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.reggie.common.R;
import com.demo.reggie.pojo.Employee;
import com.demo.reggie.service.EmployeeService;
import com.demo.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果用户名或密码错误，返回登录失败
        if(emp == null || !password.equals(emp.getPassword())) return R.error("用户名或密码错误");
        //账号禁用，返回信息
        if(emp.getStatus() == 0) return R.error("账号已禁用");

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工，初始密码123456
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpSession session, @RequestBody Employee employee){
        log.info(employee.toString());
        //设置初始密码，并加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        LocalDateTime createTime = LocalDateTime.now();
        // employee.setCreateTime(createTime);
        // employee.setUpdateTime(createTime);
        // employee.setCreateUser(createUser);
        // employee.setUpdateUser(createUser);

        employeeService.save(employee);
        return R.success("添加成功");
    }

    /**
     * 分页显示用户列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, String name){
        log.info("page = {},pageSize = {}, name = {}", page, pageSize, name);

        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改用户信息，复用状态修改
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpSession session){
        // employee.setUpdateUser(empId);
        // employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("操作成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);

        if(employee != null) return R.success(employee);
        return R.error("没有查询到对应员工信息");
    }
}
