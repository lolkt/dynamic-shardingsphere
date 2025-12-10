package com.lolkt.demo.dto;
import lombok.Data;

@Data
public class OrderDto {

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
