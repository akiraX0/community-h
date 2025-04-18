package com.project.community.dto;

import com.project.community.entities.User;
import com.project.community.exceptions.UserNotFoundException;
import com.project.community.repository.UserRepository;
import com.project.community.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling authentication-related operations
 */
@Component
public class AuthenticationUtils {

    @Autowired
    private  UserRepository userRepository;



    /**
     * Gets the current authenticated user's email
     * @return The email of the currently authenticated user
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return authentication.getName();
    }

    /**
     * Gets the current authenticated user
     * @return The currently authenticated user entity
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Organizer not Found"));

        return user;
    }

    /**
     * Checks if the current user is the owner of a resource
     * @param resourceOwnerId ID of the resource owner
     * @return true if current user is the owner, false otherwise
     */
    public boolean isResourceOwner(String resourceOwnerId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().toString().equals(resourceOwnerId);
    }

    /**
     * Checks if a specific authentication object belongs to the owner of a resource
     * @param authentication The authentication object to check
     * @param resourceOwnerId ID of the resource owner
     * @return true if the authenticated user is the owner, false otherwise
     */
    public boolean isResourceOwner(Authentication authentication, String resourceOwnerId) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UserNotFoundException("Organizer not Found"));
        return user.getId().toString().equals(resourceOwnerId);
    }
}