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


import com.lolkt.demo.entity.OrderPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author EDY
 */
public interface OrderMapper {

    @Select("select * from t_order where id =#{id}")
    OrderPO selectById(Integer id);

    @Select("select * from t_order")
    List<OrderPO> selectOrders();

    @Insert("insert into t_order (remark,customer_id) values (#{remark},#{customerId})")
    boolean addOrder(@Param("remark") String remark, @Param("customerId") Integer customerId);

    @Update("update t_order set name =#{name} where id =#{id}")
    void updateOrder(OrderPO Order);

    @Delete("delete from t_order where id = #{id}")
    void deleteOrderById(Long id);

    @Delete("delete from t_order")
    void deleteAll();

}
