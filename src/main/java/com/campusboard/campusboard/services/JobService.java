package com.campusboard.campusboard.services;

import com.campusboard.campusboard.exceptions.JobNotFoundException;
import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // ===========================
    // CREATE NEW JOB (EMPLOYER)
    // ===========================
    public Job createJob(Job job, User employer) {
        job.setEmployer(employer);
        job.setStatus(Job.Status.PENDING);
        return jobRepository.save(job);
    }

    // ===========================
    // UPDATE JOB
    // ===========================
    public Job updateJob(Long jobId, Job updatedJob) {
        Job existing = getJobById(jobId);

        existing.setTitle(updatedJob.getTitle());
        existing.setDescription(updatedJob.getDescription());
        existing.setLocation(updatedJob.getLocation());
        existing.setSalary(updatedJob.getSalary());
        existing.setCategory(updatedJob.getCategory());
        existing.setDeadline(updatedJob.getDeadline());

        return jobRepository.save(existing);
    }

    // ===========================
    // DELETE JOB
    // ===========================
    public void deleteJob(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new JobNotFoundException("Job not found: " + jobId);
        }
        jobRepository.deleteById(jobId);
    }

    // ===========================
    // FIND BY ID
    // ===========================
    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));
    }

    // ===========================
    // EMPLOYER: GET THEIR JOBS
    // ===========================
    public List<Job> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    // ===========================
    // STUDENT: GET APPROVED JOBS
    // ===========================
    public List<Job> getApprovedJobs() {
        return jobRepository.findByStatus(Job.Status.APPROVED);
    }

    // ===========================
    // ADMIN: APPROVE JOB
    // ===========================
    public void approveJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setStatus(Job.Status.APPROVED);
        jobRepository.save(job);
    }

    // ===========================
    // ADMIN: REJECT JOB
    // ===========================
    public void rejectJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setStatus(Job.Status.REJECTED);
        jobRepository.save(job);
    }
}
