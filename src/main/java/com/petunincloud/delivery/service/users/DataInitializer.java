package com.petunincloud.delivery.service.users;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        RoleEntity roleClient = createRoleIfNotExists("ROLE_CLIENT");
        RoleEntity roleRestaurant = createRoleIfNotExists("ROLE_RESTAURANT");
        RoleEntity roleCourier = createRoleIfNotExists("ROLE_COURIER");
        RoleEntity roleAdmin = createRoleIfNotExists("ROLE_ADMIN");

        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Admin");
            admin.setPhone("+79231231350");
            admin.setAddress("ул. Пушкина, 10");
            admin.setRoles(Set.of(roleAdmin, roleClient)); // админ имеет все права
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("client@mail.ru").isEmpty()) {
            UserEntity client = new UserEntity();
            client.setEmail("client@mail.ru");
            client.setPassword(passwordEncoder.encode("client123"));
            client.setName("User");
            client.setPhone("+79991540301");
            client.setAddress("пр. Солнечный, 12");
            client.setRoles(Set.of(roleClient));
            userRepository.save(client);
        }

        if (userRepository.findByEmail("courier@gmail.com").isEmpty()) {
            UserEntity courier = new UserEntity();
            courier.setEmail("courier@gmail.com");
            courier.setPassword(passwordEncoder.encode("courier123"));
            courier.setName("Courier_User");
            courier.setPhone("+79920560352");
            courier.setAddress("ул. Ростовская, 102");
            courier.setRoles(Set.of(roleCourier));
            userRepository.save(courier);
        }

        if (userRepository.findByEmail("restaurant@gmail.com").isEmpty()) {
            UserEntity restaurant = new UserEntity();
            restaurant.setEmail("restaurant@gmail.com");
            restaurant.setPassword(passwordEncoder.encode("restaurant123"));
            restaurant.setName("Restaurant_User");
            restaurant.setPhone("+79992055003");
            restaurant.setAddress("ул. Ленина, 102");
            restaurant.setRoles(Set.of(roleRestaurant));
            userRepository.save(restaurant);
        }
    }

    private RoleEntity createRoleIfNotExists(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }
}