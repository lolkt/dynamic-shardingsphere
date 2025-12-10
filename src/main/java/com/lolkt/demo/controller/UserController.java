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


import com.lolkt.demo.dto.UserDto;
import com.lolkt.demo.entity.UserPO;
import com.lolkt.demo.service.UserService;
import com.lolkt.demo.support.UidGeneratorHolder;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/getAllUsers")
    @ApiOperation(value = "所有用户")
    public List<UserPO> users() {
        return userService.selectAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询用户")
    public UserPO selectById(@PathVariable  Long id) {
        return userService.selectById(id);
    }

    @PostMapping("/addUser")
    @ApiOperation(value = "新增用户")
    public UserPO addUser(@RequestBody UserDto userDto) {
        UserPO user = new UserPO();
        userDto.setId(UidGeneratorHolder.nextId());
        BeanUtils.copyProperties(userDto, user);
        userService.addUser(user);
        return user;
    }

    @PutMapping("/updUser")
    public UserPO updUser(@RequestBody UserDto userDto) {
        UserPO user = new UserPO();
        BeanUtils.copyProperties(userDto, user);
        userService.updateUser(user);
        return user;
    }

    @DeleteMapping("/deleteAll")
    public String deleteAll() {
        userService.deleteAll();
        return "成功删除所有用户";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "成功删除用户" + id;
    }
}
