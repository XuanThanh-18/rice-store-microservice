package com.toby.userservice.config;

import com.toby.userservice.entity.Role;
import com.toby.userservice.entity.User;
import com.toby.userservice.repository.RoleRepository;
import com.toby.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadRoles();
        loadUsers();
    }

    private void loadRoles() {
        if (roleRepository.count() == 0) {
            try {
                roleRepository.save(Role.builder().name(Role.ERole.ROLE_USER).build());

                roleRepository.save(Role.builder().name(Role.ERole.ROLE_ADMIN).build());

                log.info("Sample Roles loaded");
            } catch (Exception e) {
                log.error("Failed to load roles", e);
            }
        }
    }

    private void loadUsers() {
        if (userRepository.count() == 0) {
            try {
                // Create Admin user
                User adminUser = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .fullName("I am admin")
                        .phone("1234567890")
                        .address("Admin Address")
                        .build();

                Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));

                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                adminUser.setRoles(adminRoles);

                userRepository.save(adminUser);

                // Create regular user
                User regularUser =User.builder()
                        .username("user")
                        .email("user@gmail.com")
                        .password(passwordEncoder.encode("user"))
                        .fullName("I am user")
                        .phone("1234567890")
                        .address("User Address")
                        .build();

                Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: User Role not found."));

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(userRole);
                regularUser.setRoles(userRoles);

                userRepository.save(regularUser);

                log.info("Sample Users loaded");
            } catch (Exception e) {
                log.error("Failed to load users", e);
            }
        }
    }
}
