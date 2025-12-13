package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.JobApplication;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.ApplicationService;
import com.campusboard.campusboard.services.JobService;
import com.campusboard.campusboard.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Authentication auth;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test dashboard view
    @Test
    void testDashboard() {
        String view = studentController.dashboard();
        assertEquals("student/dashboard", view);
    }

    // Test viewing all approved jobs
    @Test
    void testViewApprovedJobs() {
        Job job = new Job();
        List<Job> jobs = List.of(job);
        when(jobService.getApprovedJobs()).thenReturn(jobs);

        String view = studentController.viewApprovedJobs(model);
        assertEquals("student/jobs", view);
        verify(model).addAttribute("jobs", jobs);
    }

    // Test viewing job details
    @Test
    void testViewJobDetails() {
        Job job = new Job();
        Long jobId = 1L;
        when(jobService.getJobById(jobId)).thenReturn(job);

        String view = studentController.viewJobDetails(jobId, model);
        assertEquals("student/job-details", view);
        verify(model).addAttribute("job", job);
    }

    // Test applying to a job successfully
    @Test
    void testApplyToJob_Success() throws Exception {
        User student = new User();
        Long jobId = 1L;
        when(userService.findByEmail(auth.getName())).thenReturn(student);

        String redirect = studentController.applyToJob(jobId, auth, model);
        assertEquals("redirect:/student/applications?success", redirect);
        verify(applicationService).applyToJob(jobId, student);
    }

    // Test applying to a job with exception
    @Test
    void testApplyToJob_Exception() throws Exception {
        User student = new User();
        Long jobId = 1L;
        when(userService.findByEmail(auth.getName())).thenReturn(student);
        doThrow(new RuntimeException("Already applied")).when(applicationService).applyToJob(jobId, student);

        String redirect = studentController.applyToJob(jobId, auth, model);
        assertEquals("redirect:/student/jobs/1?error=Already applied", redirect);
    }

    // Test viewing my applications
    @Test
    void testViewMyApplications() {
        User student = new User();
        List<JobApplication> apps = List.of(new JobApplication());
        when(userService.findByEmail(auth.getName())).thenReturn(student);
        when(applicationService.getApplicationsByStudent(student)).thenReturn(apps);

        String view = studentController.viewMyApplications(auth, model);
        assertEquals("student/applications", view);
        verify(model).addAttribute("applications", apps);
    }
}
