package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.entity.ShoppingCart;
import com.xu.Jerry.mapper.ShoppingCartMapper;
import com.xu.Jerry.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends
        ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
