package com.lolkt.demo.service;
import com.lolkt.demo.entity.OrderPO;

import java.util.List;

public interface OrderService {

    OrderPO selectById(Integer id);

    List<OrderPO> selectAll();

    void addOrder(OrderPO Order);

    void updateOrder(OrderPO Order);

    void deleteOrderById(Long id);

    void deleteAll();
}
