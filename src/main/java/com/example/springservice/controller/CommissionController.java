package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.entites.enmap.Commission;
import com.example.springservice.repo.*;
import com.example.springservice.service.CommissionService;
import com.example.springservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/commissions")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionService commissionService;
    private final UserRepository userRepository;
    private final CommissionRepository commissionRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CommissionCreateDTO dto, HttpServletRequest request) {
        User customer = SessionUtil.requireSessionUser(userRepository, request);
        return ResponseEntity.ok(commissionService.requestCommission(dto, customer));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<?> respondToRequest(
            @PathVariable Integer id,
            @RequestBody CommissionStatusUpdateDTO dto,
            HttpServletRequest request) {
        User artist = SessionUtil.requireSessionUser(userRepository, request);
        return ResponseEntity.ok(commissionService.respondToRequest(id, Commission.Status.valueOf(dto.status), artist));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody CommissionStatusUpdateDTO dto,
            HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        return ResponseEntity.ok(commissionService.updateStatus(id, Commission.Status.valueOf(dto.status), user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        return ResponseEntity.ok(commissionService.getDetail(id, user));
    }

    @GetMapping
    public ResponseEntity<?> getMyCommissions(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        return ResponseEntity.ok(commissionService.getMyCommissions(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommission(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        Optional<Commission> commissionOpt = commissionRepository.findById(id);
        if (commissionOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Commission not found"));
        }

        Commission commission = commissionOpt.get();
        // ตรวจสอบว่า user เป็นเจ้าของ commission หรือศิลปิน
        if (!commission.getCustomer().getUserId().equals(user.getUserId()) &&
                !commission.getArtist().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "You don't have permission to delete this commission"));
        }

        commissionRepository.delete(commission);
        return ResponseEntity.ok(Map.of("message", "Commission deleted"));
    }

}

