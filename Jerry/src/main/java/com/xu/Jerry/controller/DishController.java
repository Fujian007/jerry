package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.Jerry.common.R;
import com.xu.Jerry.dto.DishDto;
import com.xu.Jerry.entity.Category;
import com.xu.Jerry.entity.Dish;

import com.xu.Jerry.entity.DishFlavor;
import com.xu.Jerry.service.CategoryService;
import com.xu.Jerry.service.DishFlavorService;
import com.xu.Jerry.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功!");
    }

    /**
     * 分页查询dish菜品
     *
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        /**
         * 由于dish实体类不满足前端页面要求
         * 这里使用dishDto,正好合适
         */
        Page<DishDto> PageDto = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //如果name不为空，就查询条件
        queryWrapper.like(name != null, Dish::getName, name);
        //设置条件查询条件，降序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //不查询已删除的菜品
        queryWrapper.eq(Dish::getIsDeleted,0);

        dishService.page(pageInfo, queryWrapper);

        /**
         * 由于dish实体类不满足前端页面要求
         * 这里使用dishDto,对象拷贝
         * 把分页数据pageInfo拷贝到dishDtoPage中
         * 但是需要忽略 Page 中的records属性，我们需要对分页数据进行处理，把菜品id转换成名称
         */
        BeanUtils.copyProperties(pageInfo, PageDto, "records");
        List<Dish> records = pageInfo.getRecords();
        //stream()流
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            //把dish对象属性拷贝到dishDto
            DishDto dishDto = new DishDto();
            //把records中遍历的dish对象属性拷贝到dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //如果category不为空
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        PageDto.setRecords(dishDtoList);

        return R.success(PageDto);
    }

    /**
     * dish菜品修改的时候，回显数据
     */

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);

    }

    //修改菜品属性
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 批量修改菜品状态
     * 修改菜品状态status，1：表示起售。0：停售
     * http://localhost:8080/dish/status/0?ids=1526400815027957761
     *
     * @param sta 要修改的状态
     * @param ids 批量修改菜品id
     * @return
     */
    @PostMapping("/status/{sta}")
    public R<String> status(@PathVariable int sta, Long[] ids) {
        log.info("要修改成状态: {}", sta);
        log.info("要修改状态的ids: {}", Arrays.toString(ids));

        //把dish表中的id等于dishIds数组的那些的status修改为，目标状态：sta

        for (Long id : ids) {
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Dish::getId,id).set(Dish::getStatus, sta);
            dishService.update(updateWrapper);
        }
        return R.success("修改状态成功");
    }

    /**
    * 删除菜品，并非真正删除，只需要把dish表中的isDeleted字段设置为1即可
    */
    @DeleteMapping
    public R<String> deleteDish(Long[] ids){
        System.out.println(Arrays.toString(ids));
        log.info("要删除的ids{}",Arrays.toString(ids));

        //把dish表中的id等于Ids数组的那些的IsDeleted属性修改为1
        for (Long id : ids) {
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Dish::getId,id).set(Dish::getIsDeleted, 1);
            dishService.update(updateWrapper);
        }
        return R.success("删除成功");
    }


    /**
     * 当添加套餐时，用于展示菜品列表
     * 或者或者搜索用户名
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        log.info("Category: {}",dish.getCategoryId());
//        //获取到传过来的菜品id，或者(使用搜索情况时会传入name)name
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        Long categoryId = dish.getCategoryId();
//        String dishName = dish.getName();
//        queryWrapper.eq(categoryId != null,Dish::getCategoryId,dish.getCategoryId());
//
//        //使用搜索情况时会传入name
//        queryWrapper.like(dishName != null,Dish::getName,dish.getName());
//        //按照sort字段排序，如果相同按照最后一次更新时间
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        //只查询状态是1的菜品,也就是在售卖的
//        queryWrapper.eq(Dish::getStatus,1);
//        List<Dish> dishList = dishService.list(queryWrapper);
//        return R.success(dishList);
//    }

    /**
     * 当添加套餐时，用于展示菜品列表
     * 或者或者搜索用户名
     * 支持移动端菜品展示，需要返回Dish
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info("Category: {}",dish.getCategoryId());
        //获取到传过来的菜品id，或者(使用搜索情况时会传入name)name
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = dish.getCategoryId();
        String dishName = dish.getName();
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,dish.getCategoryId());
        //使用搜索情况时会传入name
        queryWrapper.like(dishName != null,Dish::getName,dish.getName());
        //按照sort字段排序，如果相同按照最后一次更新时间
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //只查询状态是1的菜品,也就是在售卖的
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = dishService.list(queryWrapper);
        //把每个Dish对象复制给DishDto对象
        //使用Stream流
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            //创建dishDto对象
            DishDto dishDto = new DishDto();
            //把每个Dish对象复制给DishDto对象
            BeanUtils.copyProperties(item,dishDto);
            //获取dish的id
            Long dishId = item.getId();
            //获取dish对应的口味表
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            //select * from DishFlavor where dishId = ?
            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }


}
