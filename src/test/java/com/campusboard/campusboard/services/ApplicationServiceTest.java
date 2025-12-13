package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.DuplicateApplicationException;
import com.campusboard.campusboard.exceptions.JobNotFoundException;
import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.JobApplication;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.JobApplicationRepository;
import com.campusboard.campusboard.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Job job;
    private User student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        job = new Job();
        student = new User();
    }

    // Test applying to a job successfully
    @Test
    void testApplyToJob_Success() {
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobAndStudent(job, student)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> applicationService.applyToJob(1L, student));
        verify(applicationRepository).save(any(JobApplication.class));
    }

    // Test applying to a job that doesn't exist
    @Test
    void testApplyToJob_JobNotFound() {
        when(jobRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> applicationService.applyToJob(1L, student));
    }

    // Test applying to a job twice
    @Test
    void testApplyToJob_DuplicateApplication() {
        JobApplication existingApp = new JobApplication();
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(job));
        when(applicationRepository.findByJobAndStudent(job, student)).thenReturn(Optional.of(existingApp));

        assertThrows(DuplicateApplicationException.class, () -> applicationService.applyToJob(1L, student));
    }

    // Test retrieving applications for a job
    @Test
    void testGetApplicationsForJob() {
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(job));
        when(applicationRepository.findByJob(job)).thenReturn(List.of(new JobApplication()));

        List<JobApplication> apps = applicationService.getApplicationsForJob(1L);
        assertEquals(1, apps.size());
    }

    // Test retrieving applications by student
    @Test
    void testGetApplicationsByStudent() {
        when(applicationRepository.findByStudent(student)).thenReturn(List.of(new JobApplication()));

        List<JobApplication> apps = applicationService.getApplicationsByStudent(student);
        assertEquals(1, apps.size());
    }
}
