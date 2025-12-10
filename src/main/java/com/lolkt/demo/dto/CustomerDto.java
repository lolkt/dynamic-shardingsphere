package com.lolkt.demo.dto;

import lombok.Data;

@Data
public class CustomerDto {

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
