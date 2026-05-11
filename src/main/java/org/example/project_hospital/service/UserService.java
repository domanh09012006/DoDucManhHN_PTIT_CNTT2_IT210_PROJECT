package org.example.project_hospital.service;

import org.example.project_hospital.dto.AuthDTO;
import org.example.project_hospital.entity.Role;
import org.example.project_hospital.entity.User;
import org.example.project_hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean register(AuthDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(Role.PATIENT);

        userRepository.save(user);
        return true;
    }

    public User login(String username, String rawPassword) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            return null;
        }

        User existingUser = user.get();
        boolean isPasswordMatch = passwordEncoder.matches(rawPassword, existingUser.getPassword());

        if (isPasswordMatch) {
            return existingUser;
        } else {
            return null;
        }
    }
}