package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.Job;
import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.JobService;
import com.campusboard.campusboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    // ===========================================
    // ADMIN DASHBOARD
    // ===========================================
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // ===========================================
    // VIEW PENDING JOBS
    // ===========================================
    @GetMapping("/jobs/pending")
    public String viewPendingJobs(Model model) {

        List<Job> pendingJobs = jobService.getApprovedJobs()
                .stream()
                .filter(job -> job.getStatus() == Job.Status.PENDING)
                .toList();

        model.addAttribute("jobs", pendingJobs);
        return "admin/pending-jobs";
    }

    // -------------------------------------------
    // *Alternate more accurate version*
    // If you prefer, replace above section with:
    /*
    @GetMapping("/jobs/pending")
    public String viewPendingJobs(Model model) {
        List<Job> pendingJobs = jobService.getJobsByStatus(Job.Status.PENDING);
        model.addAttribute("jobs", pendingJobs);
        return "admin/pending-jobs";
    }
    */
    // -------------------------------------------

    // ===========================================
    // APPROVE JOB
    // ===========================================
    @PostMapping("/jobs/approve/{jobId}")
    public String approveJob(@PathVariable Long jobId) {
        jobService.approveJob(jobId);
        return "redirect:/admin/jobs/pending?approved";
    }

    // ===========================================
    // REJECT JOB
    // ===========================================
    @PostMapping("/jobs/reject/{jobId}")
    public String rejectJob(@PathVariable Long jobId) {
        jobService.rejectJob(jobId);
        return "redirect:/admin/jobs/pending?rejected";
    }

    // ===========================================
    // MANAGE USERS
    // ===========================================
    @GetMapping("/users")
    public String manageUsers(Model model) {

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "admin/manage-users";
    }

    // ===========================================
    // ACTIVATE USER
    // ===========================================
    @PostMapping("/users/activate/{id}")
    public String activateUser(@PathVariable Long id) {
        userService.updateStatus(id, User.Status.ACTIVE);
        return "redirect:/admin/users?activated";
    }

    // ===========================================
    // DEACTIVATE USER
    // ===========================================
    @PostMapping("/users/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id) {
        userService.updateStatus(id, User.Status.INACTIVE);
        return "redirect:/admin/users?deactivated";
    }
}
