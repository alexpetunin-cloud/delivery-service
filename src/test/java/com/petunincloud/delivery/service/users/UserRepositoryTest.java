package com.petunincloud.delivery.service.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        createUser(
                "Михаил",
                "user1@gmail.com",
                "+79123456789",
                "ул. Проспектная, 5",
                "01234"
        );
        createUser(
                "Евгений",
                "user2@gmail.com",
                "+79923452789",
                "ул. Древесная, 25",
                "012345"
        );
        createUser(
                "Александр",
                "user3@gmail.com",
                "+79023356539",
                "ул. Рожнина, 55",
                "1234567"
        );
    }

    private void createUser(
            String name,
            String email,
            String phone,
            String address,
            String password
    ) {
        UserEntity user = new UserEntity();

        user.setEmail(email);
        user.setPhone(phone);
        user.setName(name);
        user.setAddress(address);
        user.setPassword(password);

        entityManager.persist(user);
    }

    @Test
    void searchAllByFilter_ShouldReturnUserByName() {
        Pageable pageable = PageRequest.of(0, 5);

        List<UserEntity> users = userRepository.searchAllByFilter(
                "Алекс",
                null,
                null,
                null,
                pageable
        );

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("Александр");
    }

    @Test
    void searchAllByFilter_ShouldReturnUserByEmail() {
        Pageable pageable = PageRequest.of(0, 5);

        List<UserEntity> users = userRepository.searchAllByFilter(
                null,
                "user1@gmail.com",
                null,
                null,
                pageable
        );

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    void searchAllByFilter_ShouldReturnUserByPhone() {
        Pageable pageable = PageRequest.of(0, 5);

        List<UserEntity> users = userRepository.searchAllByFilter(
                null,
                null,
                "+79923452789",
                null,
                pageable
        );

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getPhone()).isEqualTo("+79923452789");
    }

    @Test
    void searchAllByFilter_ShouldReturnUserByAddress() {
        Pageable pageable = PageRequest.of(0, 5);

        List<UserEntity> users = userRepository.searchAllByFilter(
                null,
                null,
                null,
                "ул. Проспект",
                pageable
        );

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getAddress()).isEqualTo("ул. Проспектная, 5");
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 2);

        List<UserEntity> users = userRepository.searchAllByFilter(
                null,
                null,
                null,
                null,
                pageable
        );

        assertThat(users).hasSize(2);
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        Optional<UserEntity> user = userRepository.findByEmail("user1@gmail.com");

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty() {
        Optional<UserEntity> user = userRepository.findByEmail("user999@gmail.com");

        assertThat(user).isEmpty();
    }

    @Test
    void findByPhone_ShouldReturnUser() {
        Optional<UserEntity> user = userRepository.findByPhone("+79023356539");

        assertThat(user).isPresent();
        assertThat(user.get().getPhone()).isEqualTo("+79023356539");
    }

    @Test
    void findByPhone_ShouldReturnEmpty() {
        Optional<UserEntity> user = userRepository.findByPhone("+79000000000");

        assertThat(user).isEmpty();
    }
}
