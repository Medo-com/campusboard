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

@Service
public class ApplicationService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    // ================================
    // STUDENT APPLIES TO A JOB
    // ================================
    public JobApplication applyToJob(Long jobId, User student) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));

        // Prevent duplicate applications
        if (applicationRepository.findByJobAndStudent(job, student).isPresent()) {
            throw new DuplicateApplicationException("You already applied to this job.");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setStudent(student);

        return applicationRepository.save(application);
    }

    // ================================
    // STUDENT: VIEW THEIR APPLICATIONS
    // ================================
    public List<JobApplication> getApplicationsByStudent(User student) {
        return applicationRepository.findByStudent(student);
    }

    // ================================
    // EMPLOYER: VIEW APPLICANTS FOR A JOB
    // ================================
    public List<JobApplication> getApplicationsForJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found: " + jobId));

        return applicationRepository.findByJob(job);
    }
}
