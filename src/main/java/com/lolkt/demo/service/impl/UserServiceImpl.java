package com.lolkt.demo.service.impl;/*
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





import com.lolkt.demo.entity.UserPO;
import com.lolkt.demo.mapper.UserMapper;
import com.lolkt.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserPO selectById( Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<UserPO> selectAll() {
        return userMapper.selectUsers();
    }

    @Override
    public void addUser(UserPO user) {
        userMapper.addUser(user.getName(), user.getRole());
    }

    @Override
    public void updateUser(UserPO user) {
        userMapper.updateUser(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userMapper.deleteUserById(id);
    }

    @Override
    public void deleteAll() {
        userMapper.deleteAll();
    }
}