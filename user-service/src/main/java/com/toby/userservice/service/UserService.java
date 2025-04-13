package com.toby.userservice.service;

import com.toby.userservice.dto.request.ChangePasswordRequest;
import com.toby.userservice.dto.request.UpdateProfileRequest;
import com.toby.userservice.dto.response.MessageResponse;
import com.toby.userservice.dto.response.UserResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    UserResponse getCurrentUser();

    UserResponse getUserById(Long id);

    @Transactional
    UserResponse updateProfile(UpdateProfileRequest request);

    @Transactional
    MessageResponse changePassword(ChangePasswordRequest request);
}
