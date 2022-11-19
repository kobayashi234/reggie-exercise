package com.demo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.reggie.common.CustomException;
import com.demo.reggie.mapper.OrderMapper;
import com.demo.reggie.pojo.*;
import com.demo.reggie.service.*;
import com.demo.reggie.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得用户id
        Long userId = BaseContext.getCurrentId();
        //查询用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0)
            throw new CustomException("购物车为空，不能下单");

        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null)
            throw new CustomException("地址信息有误，不能下单");

        //生成和设置订单号
        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger();//线程安全的累加工具类
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(cart.getNumber());
            orderDetail.setDishFlavor(cart.getDishFlavor());
            orderDetail.setDishId(cart.getDishId());
            orderDetail.setSetmealId(cart.getSetmealId());
            orderDetail.setName(cart.getName());
            orderDetail.setImage(cart.getImage());
            orderDetail.setAmount(cart.getAmount());
            //金额累加
            amount.addAndGet(cart.getAmount().multiply(new BigDecimal(cart.getNumber())).intValue());
            orderDetails.add(orderDetail);
        }
        //保存订单明细数据
        orderDetailService.saveBatch(orderDetails);

        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);//已付款，带派送
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserName(user.getName());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setConsignee(addressBook.getConsignee());

        //保存订单数据
        this.save(orders);
        //清空购物车(wrapper之前已经添加过查询条件)
        shoppingCartService.remove(queryWrapper);
    }
}
