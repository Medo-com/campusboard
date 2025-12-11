package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.DuplicateApplicationException;
import com.campusboard.campusboard.exceptions.JobNotFoundException;
import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.JobApplication;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.JobApplicationRepository;
import com.campusboard.campusboard.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ============================================
 * APPLICATION SERVICE
 * ============================================
 * This service handles all business logic related to
 * job applications submitted by students.
 *
 * Key responsibilities:
 * - Allow students to apply to jobs
 * - Prevent duplicate applications
 * - Retrieve applications by student or job
 * ============================================
 */
@Service
public class ApplicationService {

    // =============================
    // DEPENDENCY INJECTION
    // =============================
    // Repositories are automatically injected by Spring
    @Autowired
    private JobApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    /**
     * =============================
     * APPLY TO A JOB
     * =============================
     * Allows a student to apply to a specific job.
     * Prevents duplicate applications using database constraint.
     *
     * @param jobId - ID of the job to apply to
     * @param student - Student user who is applying
     * @throws JobNotFoundException if job doesn't exist
     * @throws DuplicateApplicationException if student already applied
     */
    public void applyToJob(Long jobId, User student) {
        // Find the job by ID, throw exception if not found
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with ID: " + jobId));

        // Check if student has already applied to this job
        Optional<JobApplication> existing = applicationRepository.findByJobAndStudent(job, student);

        if (existing.isPresent()) {
            // Student already applied - throw custom exception
            throw new DuplicateApplicationException("You have already applied to this job");
        }

        // Create new application
        JobApplication application = new JobApplication();
        application.setJob(job);  // Set the job reference
        application.setStudent(student);  // Set the student reference
        application.setStatus(JobApplication.Status.SUBMITTED);  // Default status

        // Save to database
        applicationRepository.save(application);
    }

    /**
     * =============================
     * GET APPLICATIONS FOR A JOB
     * =============================
     * Retrieves all applications submitted for a specific job.
     * Used by employers to see who applied.
     *
     * @param jobId - ID of the job
     * @return List of all applications for this job
     */
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        // Find the job first
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        // Return all applications for this job
        return applicationRepository.findByJob(job);
    }

    /**
     * =============================
     * GET APPLICATIONS BY STUDENT
     * =============================
     * Retrieves all applications submitted by a specific student.
     * Used by students to view their application history.
     *
     * @param student - The student user
     * @return List of all applications by this student
     */
    public List<JobApplication> getApplicationsByStudent(User student) {
        // Query database for all applications by this student
        return applicationRepository.findByStudent(student);
    }
}