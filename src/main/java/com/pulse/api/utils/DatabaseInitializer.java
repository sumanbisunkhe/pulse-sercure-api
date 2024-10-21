package com.pulse.api.utils;

import com.pulse.api.enums.Gender;
import com.pulse.api.enums.RelationshipStatus;
import com.pulse.api.enums.RoleName;
import com.pulse.api.model.User;
import com.pulse.api.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class DatabaseInitializer {

    private final UserRepo userRepository;

    @Autowired
    public DatabaseInitializer(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("sumanbisunkhe304@gmail.com")) {
            User user = new User();
            user.setUsername("Suman");
            user.setPassword("$2a$12$CgeWqCls7y1lOl4U7umNEeBNoSUExhG2dgfJseWY27O.jlHnCKt8e");
            user.setEmail("sumanbisunkhe304@gmail.com");
            user.setFirstName("Suman");
            user.setLastName("Bisunkhe");
            user.setPhone("9840948274");
            user.setCountry("Nepal");
            user.setProfilePicture("prof.jpg");
            user.setGender(Gender.MALE);
            user.setRelationshipStatus(RelationshipStatus.SINGLE);
            user.setBio("This is Suman's bio.");

            // Setting roles
            HashSet<RoleName> roles = new HashSet<>();
            roles.add(RoleName.ADMIN);
            roles.add(RoleName.NORMAL);
            user.setRoles(roles);

            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
        }
    }
}
