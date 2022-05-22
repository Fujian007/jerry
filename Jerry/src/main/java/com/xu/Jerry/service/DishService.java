package com.xu.Jerry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.Jerry.dto.DishDto;
import com.xu.Jerry.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增dish表和口味表，因为两者表是相互关联的
    void saveWithFlavor(DishDto dishDto);


    //查询dish和dishFlavor表，合成一个dishDto返回给页面
    DishDto getWithFlavor(Long id);

    //更新dish菜品表和口味dishFlavor表
    void updateWithFlavor(DishDto dishDto);


    //

}
