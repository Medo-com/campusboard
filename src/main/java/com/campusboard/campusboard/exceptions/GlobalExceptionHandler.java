package com.campusboard.campusboard.exceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ================================
    // USER NOT FOUND
    // ================================
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    // ================================
    // JOB NOT FOUND
    // ================================
    @ExceptionHandler(JobNotFoundException.class)
    public String handleJobNotFound(JobNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    // ================================
    // DUPLICATE APPLICATION
    // ================================
    @ExceptionHandler(DuplicateApplicationException.class)
    public String handleDuplicateApplication(DuplicateApplicationException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    // ================================
    // GENERIC EXCEPTION (fallback)
    // ================================
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {

        model.addAttribute("errorMessage",
                "Something went wrong. Please try again or contact support.");

        return "error/custom-error";
    }
}
