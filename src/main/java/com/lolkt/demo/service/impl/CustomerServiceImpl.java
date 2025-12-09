package com.lolkt.demo.service.impl;

import com.lolkt.demo.entity.CustomerPO;
import com.lolkt.demo.mapper.CustomerMapper;
import com.lolkt.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CustomerPO selectById(Integer id) {
        return customerMapper.selectById(id);
    }

    @Override
    public List<CustomerPO> selectAll() {
        return customerMapper.selectCustomers();
    }

    @Override
    public void addCustomer(CustomerPO customer) {
        customerMapper.addCustomer(customer.getId(),customer.getMobile(), customer.getAge());
    }

    @Override
    public void updateCustomer(CustomerPO customer) {
        customerMapper.updateCustomer(customer);
    }

    @Override
    public void deleteCustomerById(Long id) {
        customerMapper.deleteCustomerById(id);
    }

    @Override
    public void deleteAll() {
        customerMapper.deleteAll();
    }
}