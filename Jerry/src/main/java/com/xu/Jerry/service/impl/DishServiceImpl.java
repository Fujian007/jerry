package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.common.R;
import com.xu.Jerry.dto.DishDto;
import com.xu.Jerry.entity.Dish;
import com.xu.Jerry.entity.DishFlavor;
import com.xu.Jerry.mapper.DishMapper;
import com.xu.Jerry.service.DishFlavorService;
import com.xu.Jerry.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends
        ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //保存菜品,同时保存对应的口味
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息，到dish表中
        this.save(dishDto);

        //获取菜品id
        Long id = dishDto.getId();

        //获取菜品对应的口味dishFlavor，是一个集合
        //由于前端传过来的数据中不包含dishFlavor表中的dish_id属性，也不可能包含
        //因为dishFlavor表中的dish_id字段是和dish表中的id主键保持一致，dish中的主键
        //是保存到数据库时才生成的，前端也不可能知道。
        //把每种口味都设置上对应的菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存对应口味，到DishFlavor表中
        dishFlavorService.saveBatch(flavors);
    }



    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //1,首先更新dish菜品表,因为DishDto继承了dish，所以直接操作dishDto
        this.updateById(dishDto);

        //2,因为口味表已经改变，有可能把口味删除，所以先清理菜品id对应的口味
        Long dishId = dishDto.getId();//获取菜品id
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(wrapper);

        //3,获取口味,并执行插入语句insert into dishFlavor values(?,?....);
        List<DishFlavor> flavors = dishDto.getFlavors();

        //4,给dishFlavor中的dish_id属性赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    //查询展示dish菜品表和口味情况
    //需要查询两张表
    @Override
    public DishDto getWithFlavor(Long id) {
        //查询菜品从dish表
        Dish dish = this.getById(id);

        //查询当前菜品对应的口味信息，从dishFlavor表中查
        LambdaQueryWrapper<DishFlavor> dfWrapper = new LambdaQueryWrapper<>();
        dfWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(dfWrapper);

        DishDto dishDto = new DishDto();

        //对象拷贝
        BeanUtils.copyProperties(dish,dishDto);
        //再把口味list表设置给DishDto
        dishDto.setFlavors(flavors);

        return dishDto;
    }



}
