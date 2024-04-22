package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //接口里面声明一个方法，同时操作dish，dish—flavor两张表
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询对应的菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品和口味信息
    public void upadteWithFlavor(DishDto dishDto);

    void updateBatchById(List<Object> collect);
}
