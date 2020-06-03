package com.willa.babytun.dao;

import com.willa.babytun.entity.Order;

public interface OrderDAO {
    public void insert(Order order);
    public Order findByOrderNo(String OrderNo);
}
