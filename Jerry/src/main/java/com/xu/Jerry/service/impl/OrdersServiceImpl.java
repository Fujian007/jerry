package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.common.BaseContext;
import com.xu.Jerry.common.CustomException;
import com.xu.Jerry.entity.*;
import com.xu.Jerry.mapper.OrdersMapper;
import com.xu.Jerry.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends
        ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //根据用户id查询购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if ( shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，无法下单");
        }
        //获取用户信息
        User user = userService.getById(userId);
        //获取地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook address = addressBookService.getById(addressBookId);
        if (address == null){
            throw  new CustomException("地址不能为空");
        }

        //生成订单号
        long orderID = IdWorker.getId();//

        //处理总金额
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList =  shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderID);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //保存订单数据，向Orders表中插入一条数据
        orders.setId(orderID);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //计算总金额
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderID));//订单号
        orders.setUserName(user.getName());
        orders.setConsignee(address.getConsignee());//收货人，从默认地址去获取
        orders.setPhone(address.getPhone());//设置手机号
        orders.setAddress(address.getProvinceName() == null ? "": address.getProvinceName()
                + (address.getCityName() == null ? "":address.getCityName())
                + (address.getDistrictName() == null ? "":address.getDistrictName())
                + (address.getDetail() == null? "":address.getDetail()));

        this.save(orders);
        //向订单明细表中插入多行数据
        orderDetailService.saveBatch(orderDetailList);
        //删除购物车
        shoppingCartService.remove(queryWrapper);
    }
}
