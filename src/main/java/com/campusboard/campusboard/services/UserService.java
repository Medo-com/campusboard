package com.campusboard.campusboard.services;

import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.UserRepository;
import com.campusboard.campusboard.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =====================
    // REGISTER USER
    // =====================
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // =====================
    // FIND USER BY EMAIL
    // =====================
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    // =====================
    // FIND USER BY ID
    // =====================
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    // =====================
    // ADMIN: GET ALL USERS
    // =====================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =====================
    // ADMIN: ACTIVATE/DEACTIVATE
    // =====================
    public void updateStatus(Long id, User.Status status) {
        User user = getById(id);
        user.setStatus(status);
        userRepository.save(user);
    }
}
