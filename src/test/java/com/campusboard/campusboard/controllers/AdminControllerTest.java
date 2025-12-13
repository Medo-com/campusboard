package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.JobService;
import com.campusboard.campusboard.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test dashboard view
    @Test
    void testDashboard() {
        String view = adminController.dashboard();
        assertEquals("admin/dashboard", view);
    }

    // Test pending jobs
    @Test
    void testViewPendingJobs() {
        Job job1 = new Job();
        job1.setStatus(Job.Status.PENDING);
        List<Job> jobs = List.of(job1);

        when(jobService.getJobsByStatus(Job.Status.PENDING)).thenReturn(jobs);

        String view = adminController.viewPendingJobs(model);
        assertEquals("admin/pending-jobs", view);
        verify(model).addAttribute("jobs", jobs);
    }

    // Test approve job
    @Test
    void testApproveJob() {
        Long jobId = 1L;
        String redirect = adminController.approveJob(jobId);
        assertEquals("redirect:/admin/jobs/pending?approved", redirect);
        verify(jobService).approveJob(jobId);
    }

    // Test reject job
    @Test
    void testRejectJob() {
        Long jobId = 1L;
        String redirect = adminController.rejectJob(jobId);
        assertEquals("redirect:/admin/jobs/pending?rejected", redirect);
        verify(jobService).rejectJob(jobId);
    }

    // Test manage users
    @Test
    void testManageUsers() {
        User user = new User();
        List<User> users = List.of(user);

        when(userService.getAllUsers()).thenReturn(users);

        String view = adminController.manageUsers(model);
        assertEquals("admin/manage-users", view);
        verify(model).addAttribute("users", users);
    }

    // Test activate user
    @Test
    void testActivateUser() {
        Long userId = 1L;
        String redirect = adminController.activateUser(userId);
        assertEquals("redirect:/admin/users?activated", redirect);
        verify(userService).updateStatus(userId, User.Status.ACTIVE);
    }

    // Test deactivate user
    @Test
    void testDeactivateUser() {
        Long userId = 1L;
        String redirect = adminController.deactivateUser(userId);
        assertEquals("redirect:/admin/users?deactivated", redirect);
        verify(userService).updateStatus(userId, User.Status.INACTIVE);
    }
}
