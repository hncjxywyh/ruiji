package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private  DishService dishService;
    @Resource
    private DishService dish;
    @Resource
    private DishFlavorService dishFlavor;

    @Resource
    private CategoryService categoryService;
    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);


        return  R.success("菜品增加成功！");
    }

    /**
     * 菜品的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page>  page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<DishDto> dishDtoPage=new Page<>();
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);

            }

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);


        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("要查询的id：{}",id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);


        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> upadte(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.upadteWithFlavor(dishDto);


        return  R.success("菜品增加成功！");
    }


    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){


        dishService.removeByIds(ids);
        return R.success("删除成功！");



    }

    /**
     * 更改请求状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public  R<String> status( @PathVariable int status, @RequestParam List<Long> ids){
        log.info("请求的状态为：{}，ids为：{}",status,ids);
        List<Dish> dishes = dishService.listByIds(ids);
        List<Object> collect = dishes.stream().map(item -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());


        dishService.updateBatchById(collect);

        return R.success("更新成功！");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);

            }

            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);

            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);


            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dtoList);
    }






}
