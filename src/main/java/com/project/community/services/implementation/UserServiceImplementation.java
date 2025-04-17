package com.project.community.services.implementation;

import com.project.community.dto.UserRequest;
import com.project.community.dto.UserResponse;
import com.project.community.dto.UserRole;
import com.project.community.entities.User;
import com.project.community.exceptions.EmailAlreadyExistsException;
import com.project.community.repository.UserRepository;
import com.project.community.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserResponse UserRegister(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with this email already exists");
        }
        User newUser = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .role(UserRole.VOLUNTEER)
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        userRepository.save(newUser);
        return  UserResponse.builder()
                .email(newUser.getEmail())
                .build();
    }
    @Override
    public UserResponse AdminRegister(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with this email already exists");
        }
        User newUser = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .role(UserRole.ORGANIZER)
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        userRepository.save(newUser);
        return UserResponse.builder()
                .email(newUser.getEmail())
                .build();
    }

}
