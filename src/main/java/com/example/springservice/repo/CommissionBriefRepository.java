package com.example.springservice.repo;

import com.example.springservice.entites.CommissionBrief;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 📁 repo/CommissionBriefRepository.java
public interface CommissionBriefRepository extends JpaRepository<CommissionBrief, Integer> {
    List<CommissionBrief> findByCommission_CommissionId(Integer commissionId);
}
