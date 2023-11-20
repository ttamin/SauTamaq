package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.UserDto;
import com.example.sautamaq.model.User;
import com.example.sautamaq.service.UserServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserByEmail(String email);
    void registerUser(UserDto userDto);
    void deleteUserById(Long id);
    // TODO: changePassword, changeRole, updatePassword, forgotPassword


}
