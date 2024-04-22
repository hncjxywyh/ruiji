package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.service.CategoryService;
import com.itheima.service.SetMealDishService;
import com.itheima.service.SetMealService;
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
@RequestMapping("/setmeal")
public class SetMealController {
    @Resource
    SetMealDishService setMealDishService;
    @Resource
    SetMealService setMealService;
    @Resource
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setMealService.saveWithDish(setmealDto);


        return R.success("新增套餐成功！");
    }

    /**
     * 分页查询
     * @return
     */
    @GetMapping("/page")
    public  R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo =new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        setMealService.page(pageInfo,queryWrapper);


        BeanUtils.copyProperties(pageInfo,dtoPage,                                                                                                          "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();

                setmealDto.setCategoryName(categoryName);

            }

            return setmealDto;

        }).collect(Collectors.toList());

       dtoPage.setRecords(collect);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("要删除的id为：{}",ids);

        setMealService.removeWithDish(ids);






        return  R.success("套餐数据删除成功！");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setMealService.list(queryWrapper);


        return R.success(list);
    }





}
