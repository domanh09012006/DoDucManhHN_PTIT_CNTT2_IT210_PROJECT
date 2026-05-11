package org.example.project_hospital.repository;

import org.example.project_hospital.entity.Prescription;
import org.example.project_hospital.entity.PrescriptionStatus;
import org.example.project_hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
	List<Prescription> findByStatus(PrescriptionStatus status);
	List<Prescription> findByStatusAndMedicalRecord_Appointment_Doctor(PrescriptionStatus status, Doctor doctor);
	long countByMedicalRecord_IdAndStatus(Long medicalRecordId, PrescriptionStatus status);
	long countByMedicalRecord_IdAndStatusNot(Long medicalRecordId, PrescriptionStatus status);

	// Lấy danh sách prescription đã xử lý (DISPENSED hoặc REJECTED)
	List<Prescription> findByStatusIn(List<PrescriptionStatus> statuses);
	List<Prescription> findByStatusInAndMedicalRecord_Appointment_Doctor(List<PrescriptionStatus> statuses, Doctor doctor);
}
