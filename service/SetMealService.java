package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐与菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
