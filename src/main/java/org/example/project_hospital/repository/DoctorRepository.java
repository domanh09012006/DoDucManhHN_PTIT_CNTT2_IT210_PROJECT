package org.example.project_hospital.repository;

import org.example.project_hospital.entity.Doctor;
import org.example.project_hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);

    boolean existsBySpecialty_Id(Long specialtyId);
//    List<Doctor> findByExperienceYearsGreaterThan(int years);
}