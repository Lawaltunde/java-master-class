package com.devlawal.user;

import java.util.UUID;

public class UserDao {
    private static final User[] users;

    static {
        users = new User[]{
                new User(UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3"), "Lawal"),
                new User(UUID.fromString("b10d126a-3608-4980-9f9c-aa179f5cebc3"), "James")

        };
    }

    public User[] getUsers() {
        return users;
    }
}
