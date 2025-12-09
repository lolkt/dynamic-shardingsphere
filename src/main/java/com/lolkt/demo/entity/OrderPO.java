package com.lolkt.demo.entity;
import lombok.Data;

@Data
public class OrderPO {

    private Integer id;

    /**
     *
     */
    private Integer customerId;

    /**
     * 备注
     */
    private String remark;


}
