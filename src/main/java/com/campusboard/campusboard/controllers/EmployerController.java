package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.JobApplication;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.ApplicationService;
import com.campusboard.campusboard.services.JobService;
import com.campusboard.campusboard.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    // ===========================================
    // EMPLOYER DASHBOARD
    // ===========================================
    @GetMapping("/dashboard")
    public String dashboard() {
        return "employer/dashboard";
    }

    // ===========================================
    // VIEW MY JOBS
    // ===========================================
    @GetMapping("/jobs")
    public String viewMyJobs(Authentication auth, Model model) {

        User employer = userService.findByEmail(auth.getName());
        List<Job> jobs = jobService.getJobsByEmployer(employer);

        model.addAttribute("jobs", jobs);
        return "employer/my-jobs";
    }

    // ===========================================
    // SHOW CREATE JOB FORM
    // ===========================================
    @GetMapping("/jobs/create")
    public String showCreateForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/create-job";
    }

    // ===========================================
    // HANDLE CREATE JOB
    // ===========================================
    @PostMapping("/jobs/create")
    public String createJob(@Valid @ModelAttribute("job") Job job,
                            BindingResult result,
                            Authentication auth) {

        if (result.hasErrors()) {
            return "employer/create-job";
        }

        User employer = userService.findByEmail(auth.getName());
        jobService.createJob(job, employer);

        return "redirect:/employer/jobs?created";
    }

    // ===========================================
    // SHOW EDIT JOB FORM
    // ===========================================
    @GetMapping("/jobs/edit/{jobId}")
    public String editJobForm(@PathVariable Long jobId, Model model) {

        Job job = jobService.getJobById(jobId);
        model.addAttribute("job", job);

        return "employer/edit-job";
    }

    // ===========================================
    // HANDLE EDIT JOB
    // ===========================================
    @PostMapping("/jobs/edit/{jobId}")
    public String updateJob(@PathVariable Long jobId,
                            @Valid @ModelAttribute("job") Job updatedJob,
                            BindingResult result) {

        if (result.hasErrors()) {
            return "employer/edit-job";
        }

        jobService.updateJob(jobId, updatedJob);
        return "redirect:/employer/jobs?updated";
    }

    // ===========================================
    // DELETE JOB
    // ===========================================
    @PostMapping("/jobs/delete/{jobId}")
    public String deleteJob(@PathVariable Long jobId) {

        jobService.deleteJob(jobId);
        return "redirect:/employer/jobs?deleted";
    }

    // ===========================================
    // VIEW APPLICANTS FOR A JOB  (FIXED)
    // ===========================================
    @GetMapping("/jobs/{jobId}/applicants")
    public String viewApplicants(@PathVariable Long jobId, Model model) {

        Job job = jobService.getJobById(jobId);                     // ← ADDED
        List<JobApplication> applicants = applicationService.getApplicationsForJob(jobId);

        model.addAttribute("job", job);                             // ← ADDED
        model.addAttribute("applications", applicants);

        return "employer/applicants";
    }
}
