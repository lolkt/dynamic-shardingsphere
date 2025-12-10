package com.lolkt.demo.service.impl;

import com.lolkt.demo.entity.OrderPO;
import com.lolkt.demo.mapper.OrderMapper;
import com.lolkt.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public OrderPO selectById( Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<OrderPO> selectAll() {
        return orderMapper.selectOrders();
    }

    @Override
    public void addOrder(OrderPO order) {
        orderMapper.addOrder(order.getRemark(), order.getCustomerId());
    }

    @Override
    public void updateOrder(OrderPO order) {
        orderMapper.updateOrder(order);
    }

    @Override
    public void deleteOrderById(Long id) {
        orderMapper.deleteOrderById(id);
    }

    @Override
    public void deleteAll() {
        orderMapper.deleteAll();
    }
}