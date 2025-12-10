package com.lolkt.demo.entity;

import lombok.Data;

@Data
public class CustomerPO {

    private Long id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 年龄
     */
    private Integer age;
}
