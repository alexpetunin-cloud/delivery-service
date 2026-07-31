package com.petunincloud.delivery.service.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        createRole("ROLE_CLIENT");
        createRole("ROLE_ADMIN");
        createRole("ROLE_RESTAURANT");

        entityManager.flush();
        entityManager.clear();
    }

    private void createRole(
            String name
    ) {
        RoleEntity role = new RoleEntity();
        role.setName(name);

        entityManager.persist(role);
    }

    @Test
    void findByName_ShouldReturnRole() {
        Optional<RoleEntity> role = roleRepository.findByName("ROLE_CLIENT");

        assertThat(role).isPresent();
        assertThat(role.get().getName()).isEqualTo("ROLE_CLIENT");
    }

    @Test
    void findByName_ShouldReturnEmpty() {
        Optional<RoleEntity> role = roleRepository.findByName("ROLE_LEV");

        assertThat(role).isEmpty();
    }
}
