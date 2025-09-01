// ✅ AdminController.java
package com.example.springservice.controller;

import com.example.springservice.*;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.entites.User;
import com.example.springservice.repo.ReportRepository;
import com.example.springservice.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReportRepository reportRepository;

    private boolean isAdmin(User user) {
        return user.getRole().equalsIgnoreCase("admin");
    }

    private ResponseEntity<?> checkAdminAccess(HttpServletRequest request) {
        Integer requesterId = SessionUtil.getUserIdFromSession(request);
        if (requesterId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        User admin = userRepository.findById(requesterId).orElse(null);
        if (admin == null || !isAdmin(admin)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        return ResponseEntity.ok(admin);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<?> banUser(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(request);
        if (!accessCheck.getStatusCode().is2xxSuccessful()) return accessCheck;

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        User targetUser = userOpt.get();
        targetUser.setStatus(User.Status.banned);
        userRepository.save(targetUser);

        return ResponseEntity.ok(Map.of("message", "User has been banned"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(request);
        if (!accessCheck.getStatusCode().is2xxSuccessful()) return accessCheck;

        List<UserListDTO> users = userRepository.findAll().stream()
                .map(UserListDTO::new)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(@RequestParam(required = false) String status) {
        List<Report> reports;
        if (status != null) {
            try {
                Report.ReportStatus s = Report.ReportStatus.valueOf(status.toUpperCase());
                reports = reportRepository.findAllByStatus(s);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
            }
        } else {
            reports = reportRepository.findAll();
        }

        List<ReportAdminDTO> response = reports.stream().map(ReportAdminDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Integer id) {
        Optional<Report> reportOpt = reportRepository.findById(id);
        if (reportOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Report not found"));

        return ResponseEntity.ok(new ReportAdminDTO(reportOpt.get()));
    }

    @PutMapping("/reports/{id}")
    public ResponseEntity<?> resolveReport(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        User admin = SessionUtil.requireSessionUser(userRepository, request);
        if (!"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        Optional<Report> reportOpt = reportRepository.findById(id);
        if (reportOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Report not found"));

        Report report = reportOpt.get();
        try {
            Report.ReportStatus newStatus = Report.ReportStatus.valueOf(body.get("status").toUpperCase());
            report.setStatus(newStatus);
            report.setResolvedBy(admin);
            report.setResolvedAt(LocalDateTime.now());
            reportRepository.save(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }

        return ResponseEntity.ok(Map.of("message", "Report updated"));
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Integer id) {
        if (!reportRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Report not found"));
        }
        reportRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Report deleted"));
    }

}