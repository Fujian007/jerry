package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.entity.OrderDetail;
import com.xu.Jerry.mapper.OrderDetailMapper;
import com.xu.Jerry.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends
        ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
