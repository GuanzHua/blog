package com.guanzh.service;

import com.guanzh.po.User;

public interface UserService {

    User checkUser(String username, String password);
}
