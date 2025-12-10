package com.lolkt.demo.controller;

import com.lolkt.demo.dto.OrderDto;
import com.lolkt.demo.entity.OrderPO;
import com.lolkt.demo.service.OrderService;
import com.lolkt.demo.support.UidGeneratorHolder;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("getAllOrders")
    public List<OrderPO> Orders() {
        return orderService.selectAll();
    }

    @GetMapping("/{id}")
    public OrderPO selectById(@PathVariable  Long id) {
        return orderService.selectById(id);
    }

    @PostMapping("/addOrder")
    public OrderPO addOrder(@RequestBody OrderDto OrderDto) {
        OrderPO Order = new OrderPO();
        Order.setId(UidGeneratorHolder.nextId());
        BeanUtils.copyProperties(OrderDto, Order);
        orderService.addOrder(Order);
        return Order;
    }

    @PutMapping("/updOrder")
    public OrderPO updOrder(@RequestBody OrderDto OrderDto) {
        OrderPO Order = new OrderPO();
        BeanUtils.copyProperties(OrderDto, Order);
        orderService.updateOrder(Order);
        return Order;
    }

    @DeleteMapping("/deleteAll")
    public String deleteAll() {
        orderService.deleteAll();
        return "成功删除所有订单";
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return "成功删除订单" + id;
    }
}
