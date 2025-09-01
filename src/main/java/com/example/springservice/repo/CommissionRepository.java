package com.example.springservice.repo;

import com.example.springservice.entites.User;
import com.example.springservice.entites.enmap.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// 📁 CommissionRepository.java
public interface CommissionRepository extends JpaRepository<Commission, Integer> {
    List<Commission> findAllByCustomerOrderByCreatedAtDesc(User customer);
    List<Commission> findAllByArtistOrderByCreatedAtDesc(User artist);
    @Query("SELECT c FROM Commission c WHERE c.customer = :customer OR c.artist = :artist ORDER BY c.createdAt DESC")
    List<Commission> findByCustomerOrArtist(@Param("customer") User customer, @Param("artist") User artist);
    List<Commission> findByArtist(User artist);
}


