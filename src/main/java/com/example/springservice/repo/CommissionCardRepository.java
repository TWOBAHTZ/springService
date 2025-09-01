package com.example.springservice.repo;

import com.example.springservice.entites.CommissionCard;
import com.example.springservice.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommissionCardRepository extends JpaRepository<CommissionCard, Integer> {
    List<CommissionCard> findByArtist(User artist);
    List<CommissionCard> findByOpenTrue();
}