package com.pulse.api.controller;

import com.pulse.api.dto.UserDto;
import com.pulse.api.exceptions.ResourceNotFoundException;
import com.pulse.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername("johndoe");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        ResponseEntity<?> response = userController.createUser(userDto, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("User 'johndoe' created successfully", responseBody.get("message"));
        assertEquals(userDto, responseBody.get("user"));
    }

    @Test
    void testCreateUser_ValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("userDto", "username", "Username is required")));

        ResponseEntity<?> response = userController.createUser(new UserDto(), bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals("Username is required", errors.get("username"));
    }

    @Test
    void testUpdateUser_Success() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setUsername("johnsmith");

        // Mock the BindingResult to not have errors
        when(bindingResult.hasErrors()).thenReturn(false);

        // Mock the service method to return the updated UserDto
        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(userDto);

        // Mock the findUserById method to return the updated userDto
        when(userService.findUserById(userId)).thenReturn(userDto);

        ResponseEntity<?> response = userController.updateUser(userId, userDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("User 'johnsmith' updated successfully", responseBody.get("message"));
        assertEquals(userDto, responseBody.get("user"));
    }



    @Test
    void testUpdateUser_ValidationErrors() {
        Long userId = 1L;
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("userDto", "username", "Username is required")));

        ResponseEntity<?> response = userController.updateUser(userId, new UserDto(), bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals("Username is required", errors.get("username"));
    }

    @Test
    void testGetUserById_Success() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setUsername("johndoe");

        when(userService.findUserById(userId)).thenReturn(userDto);

        ResponseEntity<Map<String, Object>> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals("User with id 1 fetched successfully", responseBody.get("message"));
        assertEquals(userDto, responseBody.get("user"));
    }

    @Test
    void testGetUserById_NotFound() {
        Long userId = 1L;
        when(userService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<Map<String, Object>> response = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals("User not found", responseBody.get("message"));
    }

    @Test
    void testGetAllUsers() {
        UserDto user1 = new UserDto();
        UserDto user2 = new UserDto();
        List<UserDto> userList = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(userList);

        ResponseEntity<Map<String, Object>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertEquals("All users fetched successfully", responseBody.get("message"));
        assertEquals(userList, responseBody.get("users"));
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setUsername("johndoe");

        when(userService.findUserById(userId)).thenReturn(userDto);

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("User 'johndoe' deleted successfully", responseBody.get("message"));
    }

    @Test
    void testDeleteUser_NotFound() {
        Long userId = 8L;
        when(userService.findUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        ResponseEntity<?> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("User not found", responseBody.get("message"));
    }

    @Test
    void testAuthenticateUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername("johndoe");

        when(userService.authenticateUser(any(String.class), any(String.class))).thenReturn(userDto);

        ResponseEntity<?> response = userController.authenticateUser("johndoe", "password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto authenticatedUser = (UserDto) response.getBody();
        assertEquals(userDto, authenticatedUser);
    }

    @Test
    void testUploadProfilePicture_Success() {
        int userId = 1;
        MultipartFile file = mock(MultipartFile.class);

        ResponseEntity<?> response = userController.uploadProfilePicture(userId, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Profile picture uploaded successfully", response.getBody());
        verify(userService, times(1)).updateUserProfilePicture(userId, file);
    }
}
