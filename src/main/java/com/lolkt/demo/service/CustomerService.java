package com.lolkt.demo.service;

import com.lolkt.demo.entity.CustomerPO;

import java.util.List;

public interface CustomerService {

    CustomerPO selectById(Integer id);

    List<CustomerPO> selectAll();

    void addCustomer(CustomerPO user);

    void updateCustomer(CustomerPO user);

    void deleteCustomerById(Long id);

    void deleteAll();
}
