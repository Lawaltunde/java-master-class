package com.devlawal.user;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserFakerDataAccessServiceTest {
    private Faker faker;
    private UserFakerDataAccessService underTest;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        underTest = new UserFakerDataAccessService(faker);
    }

    @Test
    void canGetTwentyUsers() {
        List<User> actual = underTest.getUsers();
        assertThat(actual).hasSize(20);
    }

    @Test
    void idsDoNotContainNull() {
        List<UUID> ids = underTest.getUsers().stream().map(User::getId).toList();
        assertThat(ids).doesNotContainNull();
    }

    @Test
    void idsAreUnique() {
        List<UUID> ids = underTest.getUsers().stream().map(User::getId).toList();
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    void namesDoNotContainNull() {
        List<String> names = underTest.getUsers().stream().map(User::getName).toList();
        assertThat(names).doesNotContainNull();
    }

    @Test
    void canGetUserById() {
        User user = underTest.getUsers().get(0);
        UUID id = user.getId();

        User actual = underTest.getUserById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    void returnsNullWhenUserNotFound() {
        User actual = underTest.getUserById(UUID.randomUUID());
        assertThat(actual).isNull();
    }

    @Test
    void usersHaveNamesFromFaker() {
        Name mockName = mock(Name.class);
        Faker mockFaker = mock(Faker.class);
        when(mockFaker.name()).thenReturn(mockName);
        when(mockName.fullName()).thenReturn("lawal hammed");

        UserFakerDataAccessService service = new UserFakerDataAccessService(mockFaker);

        assertThat(service.getUsers()).allMatch(u -> u.getName().equals("lawal hammed"));
    }
}
