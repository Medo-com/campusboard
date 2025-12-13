package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.UserNotFoundException;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    // Test registering a new user successfully
    @Test
    void testRegisterUser_Success() throws Exception {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(user);

        assertEquals(user, savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository).save(user);
    }

    // Test registering a user with an existing email
    @Test
    void testRegisterUser_EmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(Exception.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    // Test finding a user by email successfully
    @Test
    void testFindByEmail_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User found = userService.findByEmail(user.getEmail());
        assertEquals(user, found);
    }

    // Test finding a user by email that doesn't exist
    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(user.getEmail()));
    }

    // Test finding a user by ID successfully
    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);
        assertEquals(user, found);
    }

    // Test finding a user by ID that doesn't exist
    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    // Test getting all users
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
    }

    // Test updating user status successfully
    @Test
    void testUpdateStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateStatus(1L, User.Status.INACTIVE);
        assertEquals(User.Status.INACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    // Test updating status for a user that doesn't exist
    @Test
    void testUpdateStatus_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateStatus(1L, User.Status.ACTIVE));
    }
}
