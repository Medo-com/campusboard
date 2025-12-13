package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.JobNotFoundException;
import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private Job job;
    private User employer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        job = new Job();
        employer = new User();
    }

    // Test creating a job
    @Test
    void testCreateJob() {
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job savedJob = jobService.createJob(job, employer);

        assertEquals(job, savedJob);
        assertEquals(employer, savedJob.getEmployer());
        assertEquals(Job.Status.PENDING, savedJob.getStatus());
        verify(jobRepository).save(job);
    }

    // Test updating a job successfully
    @Test
    void testUpdateJob() {
        Job updatedJob = new Job();
        updatedJob.setTitle("New Title");
        updatedJob.setDescription("New Desc");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);

        Job result = jobService.updateJob(1L, updatedJob);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        verify(jobRepository).save(job);
    }

    // Test updating a job that doesn't exist
    @Test
    void testUpdateJob_NotFound() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> jobService.updateJob(1L, job));
    }

    // Test deleting a job successfully
    @Test
    void testDeleteJob() {
        when(jobRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> jobService.deleteJob(1L));
        verify(jobRepository).deleteById(1L);
    }

    // Test deleting a job that doesn't exist
    @Test
    void testDeleteJob_NotFound() {
        when(jobRepository.existsById(1L)).thenReturn(false);

        assertThrows(JobNotFoundException.class, () -> jobService.deleteJob(1L));
    }

    // Test approving a job
    @Test
    void testApproveJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);

        jobService.approveJob(1L);

        assertEquals(Job.Status.APPROVED, job.getStatus());
        verify(jobRepository).save(job);
    }

    // Test rejecting a job
    @Test
    void testRejectJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);

        jobService.rejectJob(1L);

        assertEquals(Job.Status.REJECTED, job.getStatus());
        verify(jobRepository).save(job);
    }

    // Test getting a job by ID successfully
    @Test
    void testGetJobById() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Job result = jobService.getJobById(1L);

        assertEquals(job, result);
    }

    // Test getting a job by ID that doesn't exist
    @Test
    void testGetJobById_NotFound() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(JobNotFoundException.class, () -> jobService.getJobById(1L));
    }

    // Test getting approved jobs
    @Test
    void testGetApprovedJobs() {
        when(jobRepository.findByStatus(Job.Status.APPROVED)).thenReturn(List.of(job));

        List<Job> jobs = jobService.getApprovedJobs();
        assertEquals(1, jobs.size());
    }

    // Test getting jobs by status
    @Test
    void testGetJobsByStatus() {
        when(jobRepository.findByStatus(Job.Status.PENDING)).thenReturn(List.of(job));

        List<Job> jobs = jobService.getJobsByStatus(Job.Status.PENDING);
        assertEquals(1, jobs.size());
    }

    // Test getting jobs by employer
    @Test
    void testGetJobsByEmployer() {
        when(jobRepository.findByEmployer(employer)).thenReturn(List.of(job));

        List<Job> jobs = jobService.getJobsByEmployer(employer);
        assertEquals(1, jobs.size());
    }
}
