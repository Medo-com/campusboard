package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.JobNotFoundException;
import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================
 * JOB SERVICE
 * ============================================
 * This service handles all business logic related to
 * job postings in the system.
 *
 * Key responsibilities:
 * - Create, update, delete jobs
 * - Approve/reject jobs (admin function)
 * - Retrieve jobs by status, employer, etc.
 * ============================================
 */
@Service
public class JobService {

    // =============================
    // DEPENDENCY INJECTION
    // =============================
    @Autowired
    private JobRepository jobRepository;

    /**
     * =============================
     * CREATE JOB
     * =============================
     * Creates a new job posting with PENDING status.
     * Called by employers when posting jobs.
     *
     * @param job - Job object with details
     * @param employer - User who is posting the job
     * @return The saved job with generated ID
     */
    public Job createJob(Job job, User employer) {
        // Set the employer who posted this job
        job.setEmployer(employer);

        // New jobs start as PENDING (awaiting admin approval)
        job.setStatus(Job.Status.PENDING);

        // Set creation timestamp
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        // Save to database and return
        return jobRepository.save(job);
    }

    /**
     * =============================
     * UPDATE JOB
     * =============================
     * Updates an existing job's details.
     * Called by employers when editing their jobs.
     *
     * @param jobId - ID of job to update
     * @param updatedJob - Job object with new details
     * @return The updated job
     * @throws JobNotFoundException if job doesn't exist
     */
    public Job updateJob(Long jobId, Job updatedJob) {
        // Find existing job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with ID: " + jobId));

        // Update all fields with new values
        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setSalary(updatedJob.getSalary());
        job.setCategory(updatedJob.getCategory());
        job.setDeadline(updatedJob.getDeadline());

        // Update timestamp
        job.setUpdatedAt(LocalDateTime.now());

        // Note: You might want to reset status to PENDING if job was APPROVED
        // Uncomment the line below if you want updates to require re-approval
        // if (job.getStatus() == Job.Status.APPROVED) {
        //     job.setStatus(Job.Status.PENDING);
        // }

        // Save and return
        return jobRepository.save(job);
    }

    /**
     * =============================
     * DELETE JOB
     * =============================
     * Permanently deletes a job posting.
     * Called by employers to remove their jobs.
     *
     * @param jobId - ID of job to delete
     * @throws JobNotFoundException if job doesn't exist
     */
    public void deleteJob(Long jobId) {
        // Verify job exists first
        if (!jobRepository.existsById(jobId)) {
            throw new JobNotFoundException("Job not found with ID: " + jobId);
        }

        // Delete from database
        // Note: This will also delete related applications due to cascade settings
        jobRepository.deleteById(jobId);
    }

    /**
     * =============================
     * APPROVE JOB (ADMIN FUNCTION)
     * =============================
     * Changes job status to APPROVED, making it visible to students.
     * Called by administrators.
     *
     * @param jobId - ID of job to approve
     * @throws JobNotFoundException if job doesn't exist
     */
    public void approveJob(Long jobId) {
        // Find the job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        // Set status to APPROVED
        job.setStatus(Job.Status.APPROVED);
        job.setUpdatedAt(LocalDateTime.now());

        // Save changes
        jobRepository.save(job);
    }

    /**
     * =============================
     * REJECT JOB (ADMIN FUNCTION)
     * =============================
     * Changes job status to REJECTED.
     * Employer can edit and resubmit.
     *
     * @param jobId - ID of job to reject
     * @throws JobNotFoundException if job doesn't exist
     */
    public void rejectJob(Long jobId) {
        // Find the job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        // Set status to REJECTED
        job.setStatus(Job.Status.REJECTED);
        job.setUpdatedAt(LocalDateTime.now());

        // Save changes
        jobRepository.save(job);
    }

    /**
     * =============================
     * GET JOB BY ID
     * =============================
     * Retrieves a single job by its ID.
     *
     * @param jobId - ID of the job
     * @return The job object
     * @throws JobNotFoundException if job doesn't exist
     */
    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found with ID: " + jobId));
    }

    /**
     * =============================
     * GET APPROVED JOBS
     * =============================
     * Retrieves all jobs with APPROVED status.
     * Used by students to see available jobs.
     *
     * @return List of approved jobs
     */
    public List<Job> getApprovedJobs() {
        return jobRepository.findByStatus(Job.Status.APPROVED);
    }

    /**
     * =============================
     * GET JOBS BY STATUS
     * =============================
     * Retrieves all jobs with a specific status.
     *
     * @param status - The job status to filter by
     * @return List of jobs with that status
     */
    public List<Job> getJobsByStatus(Job.Status status) {
        return jobRepository.findByStatus(status);
    }

    /**
     * =============================
     * GET JOBS BY EMPLOYER
     * =============================
     * Retrieves all jobs posted by a specific employer.
     * Used by employers to see their own jobs.
     *
     * @param employer - The employer user
     * @return List of jobs by this employer
     */
    public List<Job> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }
}