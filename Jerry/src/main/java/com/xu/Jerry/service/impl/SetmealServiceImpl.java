package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.common.CustomException;
import com.xu.Jerry.dto.SetmealDto;
import com.xu.Jerry.entity.Setmeal;
import com.xu.Jerry.entity.SetmealDish;
import com.xu.Jerry.mapper.SetmealMapper;
import com.xu.Jerry.service.SetmealDishService;
import com.xu.Jerry.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends
        ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 保存套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //1,首先保存setmeal表，由于SetmealDto继承了Setmeal，直接使用SetmealDto保存到setmeal表中
        this.save(setmealDto);

        //2,获取setmealDto中的SetmealDish对象list，
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //3,设置套餐id，
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());

        //4,保存setmealdish集合
        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 删除套餐
     */
    @Transactional
    public void removeWithDish(List<Long> ids){
        //查询套餐状态，是否可以删除
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //正常售卖时
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        long count = this.count(queryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在售卖，无法删除");
        }
        //删除setmeal表中的数据
        this.removeBatchByIds(ids);
        //可以删除，先删除setmealDish中的信息
        //delete from setmealdish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> dishQw = new LambdaQueryWrapper<>();
        dishQw.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishQw);

    }
}
