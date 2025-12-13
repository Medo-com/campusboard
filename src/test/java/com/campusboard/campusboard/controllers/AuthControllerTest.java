package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.UserService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication auth;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test login page
    @Test
    void testLoginPage() {
        String view = authController.loginPage();
        assertEquals("login", view);
    }

    // Test registration page
    @Test
    void testShowRegisterForm() {
        String view = authController.showRegisterForm(model);
        assertEquals("register", view);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    // Test successful registration
    @Test
    void testRegisterUser_Success() throws Exception {
        User user = new User();
        doReturn(false).when(bindingResult).hasErrors();

        String redirect = authController.registerUser(user, bindingResult, model);
        assertEquals("redirect:/login?registered", redirect);
        verify(userService).registerUser(user);
    }

    // Test registration with validation errors
    @Test
    void testRegisterUser_WithErrors() throws Exception {
        User user = new User();
        doReturn(true).when(bindingResult).hasErrors();

        String view = authController.registerUser(user, bindingResult, model);
        assertEquals("register", view);
        verify(userService, never()).registerUser(any());
    }

    // Test registration failure due to exception
    @Test
    void testRegisterUser_Exception() throws Exception {
        User user = new User();
        doReturn(false).when(bindingResult).hasErrors();
        doThrow(new RuntimeException("Email exists")).when(userService).registerUser(user);

        String view = authController.registerUser(user, bindingResult, model);
        assertEquals("register", view);
        verify(model).addAttribute(eq("errorMessage"), eq("Email exists"));
    }

    // Test home redirect for admin
    @Test
    void testHomeRedirect_Admin() {
        doReturn(Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();

        String redirect = authController.homeRedirect(auth);
        assertEquals("redirect:/admin/dashboard", redirect);
    }

    // Test home redirect for employer
    @Test
    void testHomeRedirect_Employer() {
        doReturn(Set.of(new SimpleGrantedAuthority("ROLE_EMPLOYER"))).when(auth).getAuthorities();

        String redirect = authController.homeRedirect(auth);
        assertEquals("redirect:/employer/dashboard", redirect);
    }

    // Test home redirect for student
    @Test
    void testHomeRedirect_Student() {
        doReturn(Set.of(new SimpleGrantedAuthority("ROLE_STUDENT"))).when(auth).getAuthorities();

        String redirect = authController.homeRedirect(auth);
        assertEquals("redirect:/student/dashboard", redirect);
    }

    // Test home redirect when not authenticated
    @Test
    void testHomeRedirect_NoAuth() {
        String redirect = authController.homeRedirect(null);
        assertEquals("redirect:/login", redirect);
    }
}
