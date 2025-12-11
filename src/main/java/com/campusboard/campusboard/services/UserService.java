package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.UserNotFoundException;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================
 * USER SERVICE
 * ============================================
 * This service handles all business logic related to
 * user accounts and authentication.
 *
 * Key responsibilities:
 * - Register new users
 * - Find users by email or ID
 * - Update user status (activate/deactivate)
 * - Password encryption
 * ============================================
 */
@Service
public class UserService {

    // =============================
    // DEPENDENCY INJECTION
    // =============================
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // BCrypt encoder from SecurityBeans

    /**
     * =============================
     * REGISTER USER
     * =============================
     * Creates a new user account with encrypted password.
     * Called when users register via the registration form.
     *
     * @param user - User object with registration details
     * @return The saved user with generated ID
     * @throws Exception if email already exists
     */
    public User registerUser(User user) throws Exception {
        // Check if email is already registered
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email is already registered");
        }

        // Encrypt the password before saving
        // NEVER store plain text passwords!
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Set default status to ACTIVE
        user.setStatus(User.Status.ACTIVE);

        // Set timestamps
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save to database and return
        return userRepository.save(user);
    }

    /**
     * =============================
     * FIND USER BY EMAIL
     * =============================
     * Retrieves a user account by email address.
     * Used for authentication and authorization.
     *
     * @param email - User's email address
     * @return The user object
     * @throws UserNotFoundException if user doesn't exist
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * =============================
     * FIND USER BY ID
     * =============================
     * Retrieves a user account by ID.
     *
     * @param userId - User's ID
     * @return The user object
     * @throws UserNotFoundException if user doesn't exist
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    /**
     * =============================
     * GET ALL USERS
     * =============================
     * Retrieves all registered users in the system.
     * Used by administrators for user management.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * =============================
     * UPDATE USER STATUS
     * =============================
     * Changes a user's account status (ACTIVE/INACTIVE).
     * Used by administrators to activate or deactivate accounts.
     *
     * @param userId - ID of user to update
     * @param status - New status (ACTIVE or INACTIVE)
     * @throws UserNotFoundException if user doesn't exist
     */
    public void updateStatus(Long userId, User.Status status) {
        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update status
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());

        // Save changes
        userRepository.save(user);
    }
}