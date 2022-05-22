package com.xu.Jerry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.Jerry.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
