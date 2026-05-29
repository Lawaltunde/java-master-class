package com.devlawal.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userDao);
    }

    @Test
    void canGetAllUsers() {
        List<User> users = List.of(
                new User(UUID.randomUUID(), "Alice"),
                new User(UUID.randomUUID(), "Bob")
        );
        when(userDao.getUsers()).thenReturn(users);

        List<User> actual = underTest.getAllUsers();

        assertThat(actual).isEqualTo(users);
        verify(userDao).getUsers();
    }

    @Test
    void canGetUserById() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Alice");
        when(userDao.getUserById(id)).thenReturn(user);

        User actual = underTest.getUserById(id);

        assertThat(actual).isEqualTo(user);
        verify(userDao).getUserById(id);
    }

    @Test
    void throwsWhenGetUserByIdWithNullId() {
        assertThatThrownBy(() -> underTest.getUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id cannot be null");
        verifyNoInteractions(userDao);
    }

    @Test
    void returnsNullWhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userDao.getUserById(id)).thenReturn(null);

        User actual = underTest.getUserById(id);

        assertThat(actual).isNull();
    }

    @Test
    void delegatesGetAllUsersToDao() {
        underTest.getAllUsers();
        verify(userDao, times(1)).getUsers();
    }
}
