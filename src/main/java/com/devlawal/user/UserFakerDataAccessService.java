package com.devlawal.user;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserFakerDataAccessService implements UserDao {
    private final Faker faker = new Faker();
    private final List<User> users = new ArrayList<>();


    @Override
    public List<User> getUsers() {
        for (int i = 0; i < 20; i++) {
            users.add(new User(UUID.randomUUID(), faker.name().fullName()));
        }
        return users;
    }

    @Override
    public User getUserById(UUID id) {
        return users.stream()
                .filter(user -> user != null && user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
