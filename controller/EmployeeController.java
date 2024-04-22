package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.itheima.service.EmployeeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")


public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 1.密码进行md5加密
         */

        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        /**
         * 2.查数据库
         */
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        /**
         * 3.没有查询到·就返回失败的结果
         */
        if(emp==null){
            return R.error("登陆失败!");
        }
        /**
         * 4.密码比对，不一致返回登陆失败结果
         */
        if(!emp.getPassword().equals(password)){
            return R.error("登陆失败！");
        }
        /**
         * 5.查看员工状态，如果已经禁用，返回员工禁用结果
         */
        if(emp.getStatus()==0){
            return R.error("账号已禁用！");
        }
        /**
         * 6.登陆成功，id存入session并返回成功信息
         */
        request.getSession().setAttribute("employee",
                emp.getId());
        return R.success(emp);
    }
    /**
     * 员工退出功能
     */

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");

        return R.success("退出成功！");
    }

    /**
     * 新增员工
     */

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息{}",employee);
        //设置初始密码123456，进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

       /* employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获得登陆人的信息
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        employeeService.save(employee);


        return R.success("新增员工成功！");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //分页构造器
        Page pageInfo=new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行排序
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    @PutMapping
    public  R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        /*Long emId =(Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(emId);*/
        employeeService.updateById(employee);
        return  R.success("员工信息修改成功！");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return  R.success(employee);
        }
       return R.error("没有查询到相关信息！");
    }



















}
