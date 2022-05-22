package com.xu.Jerry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.Jerry.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
