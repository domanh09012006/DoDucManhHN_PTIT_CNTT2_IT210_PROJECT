package org.example.project_hospital.service;

import lombok.RequiredArgsConstructor;
import org.example.project_hospital.entity.*;
import org.example.project_hospital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DispenseService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public String confirmDispense(Long prescriptionId, User currentUser) {
        try {
            Prescription pres = prescriptionRepository.findById(prescriptionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));

            if (pres.getStatus() != PrescriptionStatus.PENDING) {
                return "processed";
            }

            // Kiểm tra quyền chỉ áp dụng cho DOCTOR
            if (currentUser.getRole() == Role.DOCTOR) {
                MedicalRecord record = pres.getMedicalRecord();
                Appointment appt = (record != null) ? record.getAppointment() : null;
                if (appt == null || appt.getDoctor() == null ||
                        appt.getDoctor().getUser() == null ||
                        appt.getDoctor().getUser().getId() != currentUser.getId()) {
                    return "forbidden";
                }
            }

            Medicine med = pres.getMedicine();
            if (med == null || med.getQuantity() < pres.getDosage()) {
                return "stock";
            }

            med.setQuantity(med.getQuantity() - pres.getDosage());
            medicineRepository.save(med);

            pres.setStatus(PrescriptionStatus.DISPENSED);
            prescriptionRepository.save(pres);

            completeAppointmentIfNeeded(pres.getMedicalRecord());
            return "dispensed";

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public String rejectDispense(Long prescriptionId, User currentUser) {
        try {
            Prescription pres = prescriptionRepository.findById(prescriptionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));

            if (pres.getStatus() != PrescriptionStatus.PENDING) {
                return "processed";
            }

            if (currentUser.getRole() == Role.DOCTOR) {
                MedicalRecord record = pres.getMedicalRecord();
                Appointment appt = (record != null) ? record.getAppointment() : null;
                if (appt == null || appt.getDoctor() == null ||
                        appt.getDoctor().getUser() == null ||
                        appt.getDoctor().getUser().getId() != currentUser.getId()) {   // ← SỬA Ở ĐÂY
                    return "forbidden";
                }
            }

            pres.setStatus(PrescriptionStatus.REJECTED);
            prescriptionRepository.save(pres);

            completeAppointmentIfNeeded(pres.getMedicalRecord());
            return "rejected";

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void completeAppointmentIfNeeded(MedicalRecord medicalRecord) {
        if (medicalRecord == null || medicalRecord.getId() == null) return;

        long remain = prescriptionRepository.countByMedicalRecord_IdAndStatus(
                medicalRecord.getId(), PrescriptionStatus.PENDING);

        if (remain == 0 && medicalRecord.getAppointment() != null) {
            medicalRecord.getAppointment().setStatus("COMPLETED");
            appointmentRepository.save(medicalRecord.getAppointment());
        }
    }
}