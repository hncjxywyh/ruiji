package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

@Transactional//增加事物控制
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    DishService dishService;

    /**
     * 新增菜品，同时保存对应的口味信息
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本信息到菜品表
        this.save(dishDto);
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((items) -> {
            items.setDishId(dishDto.getId());
            return items;
        }).collect(Collectors.toList());
        //保存菜品口味信息到菜品口味表

        dishFlavorService.saveBatch(flavors);




    }

    /**
     * 根据id查询对应的菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = dishService.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //在菜品口味表中查询对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);


        return dishDto;


    }

    @Override
    @Transactional
    public void upadteWithFlavor(DishDto dishDto) {
        //将菜品信息更新
        dishService.updateById(dishDto);
        //清空口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加提交过来的口味信息

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((items) -> {
            items.setDishId(dishDto.getId());
            return items;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);







    }

    @Override
    public void updateBatchById(List<Object> collect) {
        this.updateBatchById(collect);
    }


}

