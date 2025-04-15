package com.example.springservice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(String name, String email, String password, String role) {
        String hashedPassword = passwordEncoder.encode(password); // ✅
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(role);
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // ✅ ค้นหาผู้ใช้โดย ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // ✅ ค้นหาผู้ใช้โดย Email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

