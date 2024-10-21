package com.pulse.api.controller;

import com.pulse.api.dto.UserDto;
import com.pulse.api.exceptions.ResourceNotFoundException;
import com.pulse.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @ModelAttribute UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(processErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        UserDto createdUser = userService.createUser(userDto);

        // Prepare the response body
        Map<String, Object> responseBody = new HashMap<>();
        String successMessage = "User '" + createdUser.getUsername() + "' created successfully";
        responseBody.put("message", successMessage);
        responseBody.put("user", createdUser);

        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }



    // Update an existing user
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @Valid @ModelAttribute UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(processErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        // Call the service to update the user
        userService.updateUser(id, userDto);

        // Retrieve the updated user details (you can fetch it back or return the same userDto)
        UserDto updatedUser = userService.findUserById(id);

        // Prepare the response body
        Map<String, Object> responseBody = new HashMap<>();
        String successMessage = "User '" + updatedUser.getUsername() + "' updated successfully";
        responseBody.put("message", successMessage);
        responseBody.put("user", updatedUser);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    // Get user by id
    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable("id") Long id) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            UserDto userDto = userService.findUserById(id);
            responseBody.put("message", "User with id " + id + " fetched successfully");
            responseBody.put("user", userDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            responseBody.put("message", e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable("email") String email) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            UserDto userDto = userService.findUserByEmail(email);
            responseBody.put("message", "User with email '" + email + "' fetched successfully");
            responseBody.put("user", userDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            responseBody.put("message", e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable("username") String username) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            UserDto userDto = userService.findUserByUsername(username);
            responseBody.put("message", "User with username '" + username + "' fetched successfully");
            responseBody.put("user", userDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            responseBody.put("message", e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> responseBody = new HashMap<>();
        List<UserDto> users = userService.getAllUsers();
        responseBody.put("message", "All users fetched successfully");
        responseBody.put("users", users);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        Map<String, Object> responseBody = new HashMap<>();

        try {
            // Find the user by ID
            UserDto user = userService.findUserById(id);

            // Perform the deletion
            userService.deleteUser(id);

            // Prepare the response body
            String successMessage = "User '" + user.getUsername() + "' deleted successfully";
            responseBody.put("message", successMessage);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            responseBody.put("message", e.getMessage());
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }




    // Authenticate user
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String identifier, @RequestParam String password) {
        UserDto authenticatedUser = userService.authenticateUser(identifier, password);
        return new ResponseEntity<>(authenticatedUser, HttpStatus.OK);
    }

    // Upload profile picture
    @PostMapping("/upload-profile-picture/{id}")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable("id") int id, @RequestParam("file") MultipartFile file) {
        userService.updateUserProfilePicture(id, file); // Assuming service handles this method
        return new ResponseEntity<>("Profile picture uploaded successfully", HttpStatus.OK);
    }

    // Utility method to handle errors
    private Map<String, String> processErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
