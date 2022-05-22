package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.entity.SetmealDish;
import com.xu.Jerry.mapper.SetmealDishMapper;
import com.xu.Jerry.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends
        ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
