package org.example.project_hospital.repository;
import org.example.project_hospital.entity.MedicalRecord;
import org.example.project_hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

	java.util.Optional<MedicalRecord> findByAppointment_Id(Long appointmentId);

	@Query("""
			select distinct mr from MedicalRecord mr
			join fetch mr.appointment ap
			join fetch ap.doctor d
			left join fetch mr.prescriptions p
			left join fetch p.medicine
			where ap.patient = :patient
			order by mr.createdAt desc
			""")
	List<MedicalRecord> findHistoryByPatient(@Param("patient") User patient);

	@Query("""
			select distinct mr from MedicalRecord mr
			join fetch mr.appointment ap
			join fetch ap.doctor d
			left join fetch mr.prescriptions p
			left join fetch p.medicine
			where mr.id = :recordId
			  and ap.patient = :patient
			""")
	java.util.Optional<MedicalRecord> findHistoryDetailByIdAndPatient(@Param("recordId") Long recordId,
										  @Param("patient") User patient);
}
