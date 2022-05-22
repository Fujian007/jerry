package com.xu.Jerry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.Jerry.dto.SetmealDto;
import com.xu.Jerry.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，需要操作两张表setmeal和setmealDish表
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐
     *
     */

    void removeWithDish(List<Long> ids);
}
