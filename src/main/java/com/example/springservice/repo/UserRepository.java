package com.example.springservice.repo;

import com.example.springservice.entites.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByNameContainingIgnoreCase(String name, Sort sort);

    default List<User> findAllSortedByName() {
        return findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    default List<User> searchByNameSorted(String name) {
        return findByNameContainingIgnoreCase(name, Sort.by(Sort.Direction.ASC, "name"));
    }
}
