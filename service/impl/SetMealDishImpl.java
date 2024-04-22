package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.SetmealDish;
import com.itheima.mapper.SetMealDishMapper;
import com.itheima.service.SetMealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetMealDishImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {
}
