package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.Jerry.common.R;

import com.xu.Jerry.dto.SetmealDto;
import com.xu.Jerry.entity.Category;
import com.xu.Jerry.entity.Setmeal;
import com.xu.Jerry.entity.SetmealDish;
import com.xu.Jerry.service.CategoryService;
import com.xu.Jerry.service.SetmealDishService;
import com.xu.Jerry.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐，
     * 这里会使用两张表，Setmeal和SetmealDish
     *
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        //直接调用SetMealService中的保存方法
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 展示套餐情况
     * 使用mybatisPlus框架的Page分页功能
     * 返回分页情况
     */

    @GetMapping("page")
    public R<Page<SetmealDto>> getList(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        /**
         * 由于setmeal实体类不满足前端页面要求
         * 这里使用setmealDto,正好合适
         */
        Page<SetmealDto> PageDto = new Page<>(page, pageSize);
        //1，构造条件查询
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //2，如果传入name不为空
        queryWrapper.like(name != null,Setmeal::getName,name);
        //3，设置条件查询条件，降序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //4,不查询已删除的套餐
        queryWrapper.eq(Setmeal::getIsDeleted,0);
        //5，查询结果封装到setmealPage
        setmealService.page(setmealPage,queryWrapper);

        //把setmealPage拷贝到PageDto中,忽略Page中的recode属性值
        BeanUtils.copyProperties(setmealPage,PageDto,"records");
        List<Setmeal> setmealList = setmealPage.getRecords();

        List<SetmealDto> setmealDtos = setmealList.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //把records中遍历的setmeal对象属性拷贝到dishDto中
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            //根据categoryId查询category表中的name字段
            Category category = categoryService.getById(categoryId);
            //给SetmealDto对象设置categoryName，以提供页面展示
            if (category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //把这个SetmealDto集合设置给SetmealDto的分页构造器
        PageDto.setRecords(setmealDtos);
        return R.success(PageDto);
    }


    /**
     * 修改套餐回显数据
     *
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        log.info("套餐id ：{}",id);
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询套餐内包含的菜品，应该是一个集合
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        qw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(qw);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }


    /**http://localhost:8080/setmeal/status/0?ids=1526732542548013057
     * 修改状态或者批量修改
     */
    @PostMapping("/status/{sta}")
    public R<String> updateStatus(@PathVariable int sta,Long[] ids){
        log.info("批量修改状态id：{}", Arrays.toString(ids));

        //批量修改状态
        for (Long id : ids) {
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Setmeal::getStatus,sta).eq(Setmeal::getId,id);
            setmealService.update(updateWrapper);
        }
        return R.success("修改状态成功");
    }


    /**http://localhost:8080/setmeal?ids=1526732542548013057,1415580119015145474
     * 删除单个或者批量删除
     * 只需要修改setmeal表中的is_deleted字段属性即可
     * 1: 删除
     * 0: 正常
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除套餐ids：{}",ids);
        //遍历修改is_deleted字段
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }


    /**http://localhost:8080/setmeal/list?categoryId=1413386191767674881&status=1
     * 展示套餐列表
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        Long id = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != id,Setmeal::getCategoryId,id);
        queryWrapper.eq(null != status,Setmeal::getStatus,status);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

}
