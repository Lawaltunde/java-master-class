package com.devlawal.user;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserFileDataAccessService implements UserDao, Serializable {
    private final String PATH = "src/com/devlawal/user/user.dat";
    private final File file = new File(PATH);

    // to seed user.dat with static data
    static {
        File file = new File("src/com/devlawal/user/user.dat");
        if (!file.exists()) {
            List<User> users = List.of(
                    new User(UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3"), "Lawal"),
            new User(UUID.fromString("b10d126a-3608-4980-9f9c-aa179f5cebc3"), "James"));

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(users);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}

    @Override
    public List<User> getUsers() {
        return getAllUsersFromFile();
    }

    @Override
    public User getUserById(UUID id) {
        List<User> usersFromFile = getAllUsersFromFile();

        for (User user : usersFromFile) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    private List<User> getAllUsersFromFile() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATH))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
