package com.lolkt.demo.dto;
import lombok.Data;

@Data
public class OrderDto {

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
