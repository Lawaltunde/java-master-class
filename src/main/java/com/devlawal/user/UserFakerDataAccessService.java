package com.devlawal.user;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserFakerDataAccessService implements UserDao {
    private final Faker faker;
    private final List<User> users = new ArrayList<>();

    public UserFakerDataAccessService(Faker faker) {
        this.faker = faker;
        for (int i = 0; i < 20; i++) {
            users.add(new User(UUID.randomUUID(), faker.name().fullName()));
        }
    }

    public static UserFakerDataAccessService create() {
        return new UserFakerDataAccessService(new Faker());
    }

    @Override
    public List<User> getUsers() {
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
