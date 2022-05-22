package com.xu.Jerry.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.Jerry.common.R;
import com.xu.Jerry.entity.Category;
import com.xu.Jerry.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("接收到category => " + category.toString());
        categoryService.save(category);
        return R.success("添加成功！");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //设置条件构造器，排序，根据表Category中的sort属性
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //设置条件,排序，根据表Category中的sort属性
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    //删除菜品
    @DeleteMapping()
    public R<String> delete(Long ids){
      categoryService.remove(ids);
      return R.success("分类信息删除成功");
    }


    //根据id修改菜品
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息: {}",category);
        //公共字段已经填充
        boolean b = categoryService.updateById(category);
        if (b){
            return R.success("修改成功");
        }else {
            return R.error("修改失败");
        }
    }


    /**
     * 获取菜品分类
     */

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(null != category.getType(),Category::getType,category.getType());

        //进行排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
