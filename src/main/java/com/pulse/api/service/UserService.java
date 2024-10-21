package com.pulse.api.service;

import com.pulse.api.dto.UserDto;
import com.pulse.api.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    UserDto findUserById(Long id);
    UserDto findUserByEmail(String email);
    UserDto  findUserByUsername(String username);
    List<UserDto> getAllUsers();
    void deleteUser(Long id);
    UserDto authenticateUser(String username, String password);
    public Optional<User> findByUsername(String username);
    Optional<User> findById(int id);
    List<User> findAllUsers();

    void updateUserProfilePicture(long id, MultipartFile file);
}
