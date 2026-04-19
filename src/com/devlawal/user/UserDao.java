package com.devlawal.user;

import java.util.List;
import java.util.UUID;

public interface UserDao {

    List<User> getUsers();

    User getUserById(UUID id);

}
