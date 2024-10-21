package com.pulse.api.service.impl;

import com.pulse.api.dto.UserDto;
import com.pulse.api.enums.RoleName;
import com.pulse.api.exceptions.EmailAlreadyExistsException;
import com.pulse.api.exceptions.PhoneAlreadyExistsException;
import com.pulse.api.exceptions.ResourceNotFoundException;
import com.pulse.api.exceptions.UsernameAlreadyExistsException;
import com.pulse.api.model.User;
import com.pulse.api.repo.UserRepo;
import com.pulse.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Autowired
    @Lazy
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // Check if user with the same username, email, or phone already exists
        if (userRepo.existsByUsername(userDto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username '" + userDto.getUsername() + "' already exists");
        }
        if (userRepo.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email '" + userDto.getEmail() + "' already exists");
        }
        if (userRepo.existsByPhone(userDto.getPhone())) {
            throw new PhoneAlreadyExistsException("Phone number '" + userDto.getPhone() + "' already exists");
        }

        // Convert to Entity
        User user = convertToEntity(userDto);


        // Handle profile picture upload
        handleProfilePictureUpload(userDto.getProfilePicture(), user);

        // Save User
        User savedUser = userRepo.save(user);

        // Convert to Dto
        return convertToDto(savedUser);
    }


    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        // Find the user by id
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the username or email is already taken by another user
        validateUniqueFields(userDto, id);

        // Update the existing user with the new values from userDto
        updateExistingUser(existingUser, userDto);

        // Handle profile picture upload
        handleProfilePictureUpload(userDto.getProfilePicture(), existingUser);

        // Save updated User
        userRepo.save(existingUser);
        return userDto;
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id "+id+" not found"));

        return convertToDto(user);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertToDto(user);
    }

    @Override
    public UserDto findUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));

        return convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        userRepo.delete(user);
    }

    @Transactional
    public UserDto authenticateUser(String identifier, String password) {
        User user = userRepo.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check password (you may want to implement a method for this)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return convertToDto(user);
    }

    private void handleProfilePictureUpload(MultipartFile profilePictureFile, User user) {
        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(profilePictureFile.getOriginalFilename()));
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // Generate a unique file name using UUID
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Define the path where the file will be stored
            Path uploadPath = Paths.get("D:/JAVA/pulse-api-secure UPLOADS");

            try {
                // Create directories if they don't exist
                Files.createDirectories(uploadPath);

                // Define the file path where the image will be saved
                Path filePath = uploadPath.resolve(uniqueFileName);

                // Save the file to the specified path
                Files.copy(profilePictureFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save the file name in the profilePicture field
                user.setProfilePicture(uniqueFileName);

                // Construct the URL for accessing the image
                String fileUrl = "http://pulse.com/uploads/" + uniqueFileName;


            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + uniqueFileName, e);
            }
        }
    }


    private void validateUniqueFields(UserDto userDto, Long userId) {
        Optional<User> userWithSameUsername = userRepo.findByUsername(userDto.getUsername());
        if (userWithSameUsername.isPresent() && userWithSameUsername.get().getId() != userId) {
            throw new IllegalArgumentException("Username '" + userDto.getUsername() + "' is already taken.");
        }

        Optional<User> userWithSameEmail = userRepo.findByEmail(userDto.getEmail());
        if (userWithSameEmail.isPresent() && userWithSameEmail.get().getId() != userId) {
            throw new IllegalArgumentException("Email '" + userDto.getEmail() + "' is already taken.");
        }
    }


    @Override
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepo.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public void updateUserProfilePicture(long id, MultipartFile file) {
        // Check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Fetch the user from the database using the ID
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));

        // Define a directory to save the uploaded file (or use a service like Amazon S3, etc.)
        String uploadDir = "/path/to/profile-pictures/";

        // Generate a unique filename (for example, using the user ID and original filename)
        String fileName = "profile_" + id + "_" + file.getOriginalFilename();

        // Construct the file path
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            // Save the file to the system (using NIO)
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update the user's profile picture field (assuming user has a `profilePicture` field)
            user.setProfilePicture(fileName);

            // Save the updated user back to the database
            userRepo.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload the file: " + e.getMessage());
        }
    }
    private User convertToEntity(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto cannot be null");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setMiddleName(userDto.getMiddleName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        user.setCountry(userDto.getCountry());
        user.setGender(userDto.getGender());
        user.setRelationshipStatus(userDto.getRelationshipStatus());
        user.setBio(userDto.getBio());
        // Check if profile picture is provided
        if (userDto.getProfilePicture() != null) {
            // Extract the original file name from MultipartFile
            String fileName = userDto.getProfilePicture().getOriginalFilename();
            if (fileName != null && !fileName.isEmpty()) {
                // Set the extracted file name in the user entity
                user.setProfilePicture(fileName);
            } else {
                // Handle the case where the file name is empty
                throw new RuntimeException("Invalid file name");
            }
        } else {
            // Handle the case where no file was provided
            throw new RuntimeException("Profile picture is not provided");
        }
        user.setRoles(userDto.getRoles()); // Assuming roles are set directly
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setMiddleName(user.getMiddleName());
        userDto.setLastName(user.getLastName());
        userDto.setPhone(user.getPhone());
        userDto.setCountry(user.getCountry());
        userDto.setGender(user.getGender());
        userDto.setRelationshipStatus(user.getRelationshipStatus());
        userDto.setBio(user.getBio());
        userDto.setProfilePictureUrl(user.getProfilePicture());

        Set<RoleName> roles = new HashSet<>();
        roles.add(RoleName.NORMAL);

        userDto.setRoles(roles);
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());

        return userDto;
    }

    private void updateExistingUser(User existingUser, UserDto userDto) {
        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setMiddleName(userDto.getMiddleName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setPhone(userDto.getPhone());
        existingUser.setCountry(userDto.getCountry());
        existingUser.setGender(userDto.getGender());
        existingUser.setRelationshipStatus(userDto.getRelationshipStatus());
        existingUser.setBio(userDto.getBio());
        Set<RoleName> roles = new HashSet<>();
        roles.add(RoleName.NORMAL);
        existingUser.setRoles(roles);
        existingUser.setUpdatedAt(LocalDateTime.now());
    }


}
