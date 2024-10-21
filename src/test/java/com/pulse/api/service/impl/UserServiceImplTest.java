package com.pulse.api.service.impl;

import com.pulse.api.dto.UserDto;
import com.pulse.api.enums.RoleName;
import com.pulse.api.exceptions.EmailAlreadyExistsException;
import com.pulse.api.exceptions.PhoneAlreadyExistsException;
import com.pulse.api.exceptions.ResourceNotFoundException;
import com.pulse.api.exceptions.UsernameAlreadyExistsException;
import com.pulse.api.model.User;
import com.pulse.api.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserDto mockUserDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUserDto = new UserDto();
        mockUserDto.setUsername("johndoe");
        mockUserDto.setPassword("password");
        mockUserDto.setEmail("johndoe@example.com");
        mockUserDto.setPhone("1234567890");
        mockUserDto.setRoles(Set.of(RoleName.NORMAL));

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("johndoe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setPhone("1234567890");
    }

    @Test
    void testCreateUser_Success() {
        // Mocking the MultipartFile for the profile picture
        MockMultipartFile mockProfilePicture = new MockMultipartFile(
                "profilePicture",
                "profile.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        // Mocking UserDto to include the profile picture
        mockUserDto.setProfilePicture(mockProfilePicture);

        // Mocking repository and encoder responses
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(userRepo.existsByPhone(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        // Run the createUser method
        UserDto result = userServiceImpl.createUser(mockUserDto);

        // Assertions to check the outcome
        assertEquals(mockUserDto.getUsername(), result.getUsername());
        assertEquals(mockUserDto.getEmail(), result.getEmail());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        when(userRepo.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> {
            userServiceImpl.createUser(mockUserDto);
        });
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userServiceImpl.createUser(mockUserDto);
        });
    }

    @Test
    void testCreateUser_PhoneAlreadyExists() {
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(userRepo.existsByPhone(anyString())).thenReturn(true);

        assertThrows(PhoneAlreadyExistsException.class, () -> {
            userServiceImpl.createUser(mockUserDto);
        });
    }

    @Test
    void testFindUserById_Success() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(mockUser));

        UserDto result = userServiceImpl.findUserById(1L);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testFindUserById_NotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userServiceImpl.findUserById(1L);
        });
    }

    @Test
    void testFindUserByEmail_Success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        UserDto result = userServiceImpl.findUserByEmail("johndoe@example.com");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepo, times(1)).findByEmail("johndoe@example.com");
    }

    @Test
    void testFindUserByEmail_NotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userServiceImpl.findUserByEmail("johndoe@example.com");
        });
    }

    @Test
    void testFindUserByUsername_Success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        UserDto result = userServiceImpl.findUserByUsername("johndoe");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepo, times(1)).findByUsername("johndoe");
    }

    @Test
    void testFindUserByUsername_NotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userServiceImpl.findUserByUsername("johndoe");
        });
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(mockUser));

        userServiceImpl.deleteUser(1L);

        verify(userRepo, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userServiceImpl.deleteUser(1L);
        });
    }

    @Test
    void testAuthenticateUser_Success() {
        // Set the encoded password in mockUser
        String encodedPassword = "encodedPassword";
        mockUser.setPassword(encodedPassword);

        // Mock userRepo to return the mockUser
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(mockUser));

        // Mock the passwordEncoder to return true when comparing the raw and encoded passwords
        when(passwordEncoder.matches("password", encodedPassword)).thenReturn(true);

        // Run the authenticateUser method
        UserDto result = userServiceImpl.authenticateUser("johndoe", "password");

        // Assertions to verify the result
        assertNotNull(result);
        assertEquals(mockUserDto.getUsername(), result.getUsername());
        verify(userRepo, times(1)).findByUsernameOrEmail(anyString(), anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }


    @Test
    void testAuthenticateUser_InvalidPassword() {
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            userServiceImpl.authenticateUser("johndoe", "wrongPassword");
        });
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userServiceImpl.authenticateUser("johndoe", "password");
        });
    }

    @Test
    void testGetAllUsers() {
        when(userRepo.findAll()).thenReturn(List.of(mockUser));

        List<UserDto> result = userServiceImpl.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(mockUser));

        UserDto updatedDto = new UserDto();
        updatedDto.setUsername("updatedUser");
        updatedDto.setEmail("updated@example.com");
        updatedDto.setPhone("9876543210");

        userServiceImpl.updateUser(1L, updatedDto);

        verify(userRepo, times(1)).save(mockUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userServiceImpl.updateUser(1L, mockUserDto);
        });
    }
}
