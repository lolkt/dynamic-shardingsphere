package com.lolkt.demo.mapper;/*
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


import com.lolkt.demo.entity.CustomerPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author EDY
 */
public interface CustomerMapper {

    @Select("select * from t_customer where id =#{id}")
    CustomerPO selectById( Long id);

    @Select("select * from t_customer")
    List<CustomerPO> selectCustomers();

    @Insert("insert into t_customer (id,mobile,age) values (#{id},#{mobile},#{age})")
    boolean addCustomer(@Param("id")  Long id, @Param("mobile") String mobile, @Param("age") Integer age);

    @Update("update t_customer set mobile =#{mobile} where id =#{id}")
    void updateCustomer(CustomerPO Customer);

    @Delete("delete from t_customer where id = #{id}")
    void deleteCustomerById(Long id);

    @Delete("delete from t_customer")
    void deleteAll();

}
