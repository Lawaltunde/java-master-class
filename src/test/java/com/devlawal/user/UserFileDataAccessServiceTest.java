package com.devlawal.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserFileDataAccessServiceTest {
    @TempDir
    Path tempDir;

    private UserFileDataAccessService underTest;

    private static final UUID LAWAL_ID = UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3");
    private static final UUID JAMES_ID = UUID.fromString("b10d126a-3608-4980-9f9c-aa179f5cebc3");

    @BeforeEach
    void setUp() throws Exception {
        Path tempFile = tempDir.resolve("user.dat");
        List<User> seed = List.of(
                new User(LAWAL_ID, "Lawal"),
                new User(JAMES_ID, "James")
        );
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile.toFile()))) {
            oos.writeObject(seed);
        }
        underTest = new UserFileDataAccessService(tempFile.toString());
    }

    @Test
    void canGetAllUsers() {
        assertThat(underTest.getUsers()).hasSize(2);
    }

    @Test
    void usersMatchSeedData() {
        assertThat(underTest.getUsers()).containsExactlyInAnyOrder(
                new User(LAWAL_ID, "Lawal"),
                new User(JAMES_ID, "James")
        );
    }

    @Test
    void canGetUserById() {
        User actual = underTest.getUserById(LAWAL_ID);
        assertThat(actual).isEqualTo(new User(LAWAL_ID, "Lawal"));
    }

    @Test
    void returnsNullWhenUserNotFound() {
        User actual = underTest.getUserById(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertThat(actual).isNull();
    }

    @Test
    void returnsEmptyListWhenFileDoesNotExist() {
        String nonExistentPath = tempDir.resolve("missing.dat").toString();
        UserFileDataAccessService service = new UserFileDataAccessService(nonExistentPath);
        assertThat(service.getUsers()).isEmpty();
    }
}
