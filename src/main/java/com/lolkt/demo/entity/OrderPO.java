package com.lolkt.demo.entity;
import lombok.Data;

@Data
public class OrderPO {

    private Long id;

    /**
     *
     */
    private Long customerId;

    /**
     * 备注
     */
    private String remark;


}
