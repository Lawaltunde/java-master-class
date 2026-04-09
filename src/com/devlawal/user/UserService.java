package com.devlawal.user;

import java.util.UUID;

public class UserService {
    private static UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User[] getAllUsers() {
        return userDao.getUsers();
    }

    public User getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        return userDao.getUserById(id);
    }
}
