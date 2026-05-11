package org.example.project_hospital.repository;

import org.example.project_hospital.entity.Appointment;
import org.example.project_hospital.entity.Doctor;
import org.example.project_hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctor(Doctor doctor);
    Optional<Appointment> findByIdAndPatient(Long id, User patient);
}