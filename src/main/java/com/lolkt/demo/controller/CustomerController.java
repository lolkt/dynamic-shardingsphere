package com.lolkt.demo.controller;/*
 * Copyright © ${project.inceptionYear} organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.lolkt.demo.dto.CustomerDto;
import com.lolkt.demo.entity.CustomerPO;
import com.lolkt.demo.service.CustomerService;
import com.lolkt.demo.support.UidGeneratorHolder;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/getAllCustomers")
    public List<CustomerPO> Customers() {
        return customerService.selectAll();
    }

    @GetMapping("/{id}")
    public CustomerPO selectById(@PathVariable  Long id) {
        return customerService.selectById(id);
    }

    @PostMapping("/addCustomer")
    public CustomerPO addCustomer(@RequestBody CustomerDto CustomerDto) {
        CustomerPO Customer = new CustomerPO();
        CustomerDto.setId(UidGeneratorHolder.nextId());
        BeanUtils.copyProperties(CustomerDto, Customer);
        customerService.addCustomer(Customer);
        return Customer;
    }

    @PutMapping("/updCustomer")
    public CustomerPO updCustomer(@RequestBody CustomerDto CustomerDto) {
        CustomerPO Customer = new CustomerPO();
        BeanUtils.copyProperties(CustomerDto, Customer);
        customerService.updateCustomer(Customer);
        return Customer;
    }

    @DeleteMapping("/deleteAll")
    public String deleteAll() {
        customerService.deleteAll();
        return "成功删除所有客户";
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
        return "成功删除客户" + id;
    }
}
