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
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmployerControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private Model model;

    @Mock
    private Authentication auth;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private EmployerController employerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test dashboard view
    @Test
    void testDashboard() {
        String view = employerController.dashboard();
        assertEquals("employer/dashboard", view);
    }

    // Test view my jobs
    @Test
    void testViewMyJobs() {
        User employer = new User();
        Job job = new Job();
        List<Job> jobs = List.of(job);

        when(auth.getName()).thenReturn("test@email.com");
        when(userService.findByEmail("test@email.com")).thenReturn(employer);
        when(jobService.getJobsByEmployer(employer)).thenReturn(jobs);

        String view = employerController.viewMyJobs(auth, model);
        assertEquals("employer/my-jobs", view);
        verify(model).addAttribute("jobs", jobs);
    }

    // Test show create job form
    @Test
    void testShowCreateForm() {
        String view = employerController.showCreateForm(model);
        assertEquals("employer/create-job", view);
        verify(model).addAttribute(eq("job"), any(Job.class));
    }

    // Test create job success
    @Test
    void testCreateJob_Success() {
        Job job = new Job();
        User employer = new User();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(auth.getName()).thenReturn("test@email.com");
        when(userService.findByEmail("test@email.com")).thenReturn(employer);

        String redirect = employerController.createJob(job, bindingResult, auth);
        assertEquals("redirect:/employer/jobs?created", redirect);
        verify(jobService).createJob(job, employer);
    }

    // Test create job validation error
    @Test
    void testCreateJob_WithErrors() {
        Job job = new Job();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = employerController.createJob(job, bindingResult, auth);
        assertEquals("employer/create-job", view);
        verify(jobService, never()).createJob(any(), any());
    }

    // Test edit job form
    @Test
    void testEditJobForm() {
        Job job = new Job();
        when(jobService.getJobById(1L)).thenReturn(job);

        String view = employerController.editJobForm(1L, model);
        assertEquals("employer/edit-job", view);
        verify(model).addAttribute("job", job);
    }

    // Test update job success
    @Test
    void testUpdateJob_Success() {
        Job updatedJob = new Job();
        when(bindingResult.hasErrors()).thenReturn(false);

        String redirect = employerController.updateJob(1L, updatedJob, bindingResult);
        assertEquals("redirect:/employer/jobs?updated", redirect);
        verify(jobService).updateJob(1L, updatedJob);
    }

    // Test update job validation error
    @Test
    void testUpdateJob_WithErrors() {
        Job updatedJob = new Job();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = employerController.updateJob(1L, updatedJob, bindingResult);
        assertEquals("employer/edit-job", view);
        verify(jobService, never()).updateJob(anyLong(), any());
    }

    // Test delete job
    @Test
    void testDeleteJob() {
        String redirect = employerController.deleteJob(1L);
        assertEquals("redirect:/employer/jobs?deleted", redirect);
        verify(jobService).deleteJob(1L);
    }

    // Test view applicants
    @Test
    void testViewApplicants() {
        Job job = new Job();
        JobApplication application = new JobApplication();
        List<JobApplication> applicants = List.of(application);

        when(jobService.getJobById(1L)).thenReturn(job);
        when(applicationService.getApplicationsForJob(1L)).thenReturn(applicants);

        String view = employerController.viewApplicants(1L, model);
        assertEquals("employer/applicants", view);
        verify(model).addAttribute("job", job);
        verify(model).addAttribute("applications", applicants);
    }
}
