package com.devlawal.user;

import java.util.UUID;

public class UserService {
    private static final UserDao userDao;

    static {
        userDao = new UserDao();
    }

    public User[] getAllUsers() {
        return userDao.getUsers();
    }

    public User getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        for (User user : getAllUsers()) {
            if (user != null && user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
