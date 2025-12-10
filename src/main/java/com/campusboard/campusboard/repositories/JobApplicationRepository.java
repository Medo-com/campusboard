package com.campusboard.campusboard.repositories;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.JobApplication;
import com.campusboard.campusboard.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByJob(Job job);

    List<JobApplication> findByStudent(User student);

    Optional<JobApplication> findByJobAndStudent(Job job, User student);
}
