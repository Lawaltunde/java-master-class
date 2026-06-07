package com.devlawal.user;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserArrayDataAccessServiceTest {
    private final UserArrayDataAccessService underTest = new UserArrayDataAccessService();

    @Test
    void canGetAllUsers() {
        List<User> actual = underTest.getUsers();
        List<User> expected = List.of(
                new User(UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3"), "Lawal"),
                new User(UUID.fromString("b10d126a-3608-4980-9f9c-aa179f5cebc3"), "James")
        );
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsExactlyTwoUsers() {
        assertThat(underTest.getUsers()).hasSize(2);
    }

    @Test
    void canGetUserById() {
        User actual = underTest.getUserById(UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3"));
        User expected = new User(UUID.fromString("8ca51d2b-aaaf-4bf2-834a-e02964e10fc3"), "Lawal");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenUserNotFound() {
        User actual = underTest.getUserById(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertThat(actual).isNull();
    }

    @Test
    void usersDoNotContainNull() {
        assertThat(underTest.getUsers()).doesNotContainNull();
    }
}
