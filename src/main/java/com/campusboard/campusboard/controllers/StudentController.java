package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.JobApplication;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.ApplicationService;
import com.campusboard.campusboard.services.JobService;
import com.campusboard.campusboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    // ===========================================
    // STUDENT DASHBOARD
    // ===========================================
    @GetMapping("/dashboard")
    public String dashboard() {
        return "student/dashboard";
    }

    // ===========================================
    // VIEW ALL APPROVED JOBS
    // ===========================================
    @GetMapping("/jobs")
    public String viewApprovedJobs(Model model) {
        List<Job> jobs = jobService.getApprovedJobs();
        model.addAttribute("jobs", jobs);
        return "student/jobs";
    }

    // ===========================================
    // VIEW JOB DETAILS
    // ===========================================
    @GetMapping("/jobs/{jobId}")
    public String viewJobDetails(@PathVariable Long jobId, Model model) {
        Job job = jobService.getJobById(jobId);
        model.addAttribute("job", job);
        return "student/job-details";
    }

    // ===========================================
    // APPLY TO A JOB
    // ===========================================
    @PostMapping("/apply/{jobId}")
    public String applyToJob(@PathVariable Long jobId, Authentication auth, Model model) {

        User student = userService.findByEmail(auth.getName());

        try {
            applicationService.applyToJob(jobId, student);
            return "redirect:/student/applications?success";
        } catch (Exception e) {
            return "redirect:/student/jobs/" + jobId + "?error=" + e.getMessage();
        }
    }

    // ===========================================
    // VIEW MY APPLICATIONS
    // ===========================================
    @GetMapping("/applications")
    public String viewMyApplications(Authentication auth, Model model) {

        User student = userService.findByEmail(auth.getName());
        List<JobApplication> apps = applicationService.getApplicationsByStudent(student);

        model.addAttribute("applications", apps);
        return "student/applications";
    }
}
