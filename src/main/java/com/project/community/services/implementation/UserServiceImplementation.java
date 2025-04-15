package com.project.community.services.implementation;

import com.project.community.dto.UserRequest;
import com.project.community.dto.UserResponse;
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
                .email(userRequest.getEmail())
                .roles(Arrays.asList("VOLUNTEER"))
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
                .roles(Arrays.asList("ORGANIZER"))
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        userRepository.save(newUser);
        return UserResponse.builder()
                .email(newUser.getEmail())
                .build();
    }

}
