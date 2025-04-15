package com.project.community.services.interfaces;

import com.project.community.dto.UserRequest;
import com.project.community.dto.UserResponse;

public interface UserService {
    UserResponse UserRegister(UserRequest userRequest);
    UserResponse AdminRegister(UserRequest adminRequest);
}
