package com.lolkt.demo.entity;

import lombok.Data;

@Data
public class UserPO {

    private Long id;

    /**
     * 姓名
     */
    private String name;
    /**
     * 角色
     */
    private String role;

}
