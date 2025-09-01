package com.example.springservice.util;

import com.example.springservice.SessionUtil;
import com.example.springservice.entites.User;
import com.example.springservice.entites.enmap.Commission;
import com.example.springservice.repo.CommissionRepository;
import com.example.springservice.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

// 📁 CommissionUtil.java (สำหรับ validate access)
public class CommissionUtil {
    public static ResponseEntity<?> validateAccessToCommission(
            Integer commissionId,
            HttpServletRequest request,
            CommissionRepository commissionRepository,
            UserRepository userRepository
    ) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        Optional<Commission> comOpt = commissionRepository.findById(commissionId);
        if (comOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Commission not found"));
        Commission com = comOpt.get();

        boolean authorized = user.getUserId().equals(com.getCustomer().getUserId()) ||
                user.getUserId().equals(com.getArtist().getUserId());
        if (!authorized) return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));

        return null;
    }
}
