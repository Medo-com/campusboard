package com.campusboard.campusboard.repositories;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployer(User employer);

    List<Job> findByStatus(Job.Status status);
}
