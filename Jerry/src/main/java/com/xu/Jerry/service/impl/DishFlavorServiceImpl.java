package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.entity.DishFlavor;
import com.xu.Jerry.mapper.DishFlavorMapper;
import com.xu.Jerry.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends
        ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
