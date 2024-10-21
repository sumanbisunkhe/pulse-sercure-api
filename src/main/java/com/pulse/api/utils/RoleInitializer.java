package com.pulse.api.utils;

import com.pulse.api.enums.RoleName;
import com.pulse.api.model.Role;
import com.pulse.api.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) {
        // Create and save the default roles if they do not already exist
        createRoleIfNotExists(RoleName.ADMIN);
        createRoleIfNotExists(RoleName.NORMAL);
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (!roleRepo.existsByName(roleName.toString())) { // Adjust this line based on your repo method
            Role role = new Role();
            role.setName(roleName.toString());
            roleRepo.save(role);
        }
    }
}
