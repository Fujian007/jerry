package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.common.CustomException;
import com.xu.Jerry.entity.Category;
import com.xu.Jerry.entity.Dish;
import com.xu.Jerry.entity.Setmeal;
import com.xu.Jerry.mapper.CategoryMapper;
import com.xu.Jerry.service.CategoryService;
import com.xu.Jerry.service.DishService;
import com.xu.Jerry.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends
        ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除分类之前先判断是否有关联的菜品
     * @param id
     */
    @Override
    public void remove(Long id) {
        /**
         * 查询当前分类是否关联了菜品，如果关联了菜品，则抛出一个异常
         */
        LambdaQueryWrapper<Dish> dishQW = new LambdaQueryWrapper<>();
        dishQW.eq(Dish::getCategoryId,id);
        long count1 = dishService.count(dishQW);
        if (count1 > 0){
            //说明存在关联的菜品，不能删除
            throw new CustomException("当前分类关联菜品，无法删除");
        }

        /**
         * 查询当前分类是否关联了菜品，如果关联了菜品，则抛出一个异常
         */
        LambdaQueryWrapper<Setmeal> setMealQW = new LambdaQueryWrapper<>();
        setMealQW.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(setMealQW);
        if (count2 > 0){
            throw new CustomException("当前分类关联套餐，无法删除");
        }

        //如果没有关联的项目，就可以正常删除
        super.removeById(id);

    }
}
