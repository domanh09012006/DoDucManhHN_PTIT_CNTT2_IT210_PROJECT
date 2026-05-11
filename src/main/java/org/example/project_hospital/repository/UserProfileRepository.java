package org.example.project_hospital.repository;

import org.example.project_hospital.entity.User;
import org.example.project_hospital.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
//    List<User> findByRole(org.example.project_hospital.entity.Role role);
}