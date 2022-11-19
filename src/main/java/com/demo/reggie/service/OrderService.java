package com.demo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.reggie.pojo.Orders;

public interface OrderService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);
}
