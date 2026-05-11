package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_hospital.entity.*;
import org.example.project_hospital.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    private Optional<User> getLoggedInDoctorUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.DOCTOR) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    private Doctor getLoggedInDoctor(HttpSession session) {
        Optional<User> userOpt = getLoggedInDoctorUser(session);
        if (userOpt.isEmpty()) {
            return null;
        }
        return doctorRepository.findByUser(userOpt.get()).orElse(null);
    }

    private Appointment getDoctorAppointment(Long id, Doctor doctor) {
        Appointment appt = appointmentRepository.findById(id).orElse(null);
        if (appt == null) {
            return null;
        }
        if (appt.getDoctor() == null ||
                !appt.getDoctor().getId().equals(doctor.getId())) {
            return null;
        }
        return appt;
    }

    @GetMapping("/appointments")
    public String showDoctorAppointments(HttpSession session, Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            model.addAttribute("sysError",
                    "Tài khoản của bạn chưa được thiết lập Hồ sơ chuyên môn. Vui lòng liên hệ Admin để cập nhật!");
            return "doctor/appointments";
        }
        List<Appointment> myAppointments = appointmentRepository.findByDoctor(doctor);
        model.addAttribute("appointments", myAppointments);
        return "doctor/appointments";
    }

    @GetMapping("/dispense")
    public String showDoctorDispense(HttpSession session, Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        model.addAttribute(
                "pendingPrescriptions",
                prescriptionRepository.findByStatusAndMedicalRecord_Appointment_Doctor(
                        PrescriptionStatus.PENDING,
                        doctor
                )
        );
        return "doctor/dispense";
    }

    @PostMapping("/dispense/confirm/{id}")
    @Transactional
    public String confirmDoctorDispense(@PathVariable Long id,
                                        HttpSession session) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        Prescription pres = prescriptionRepository.findById(id).orElse(null);
        if (pres == null) {
            return "redirect:/doctor/dispense?error=notfound";
        }
        if (pres.getStatus() != PrescriptionStatus.PENDING) {
            return "redirect:/doctor/dispense?error=processed";
        }
        Appointment appt = pres.getMedicalRecord().getAppointment();
        if (appt.getDoctor() == null ||
                !appt.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/dispense?error=forbidden";
        }
        Medicine med = pres.getMedicine();
        if (med.getQuantity() < pres.getDosage()) {
            return "redirect:/doctor/dispense?error=stock";
        }
        med.setQuantity(med.getQuantity() - pres.getDosage());
        medicineRepository.save(med);
        pres.setStatus(PrescriptionStatus.DISPENSED);
        prescriptionRepository.save(pres);
        long remain = prescriptionRepository.countByMedicalRecord_IdAndStatus(
                pres.getMedicalRecord().getId(),
                PrescriptionStatus.PENDING
        );
        if (remain == 0) {
            appt.setStatus("COMPLETED");
            appointmentRepository.save(appt);
        }
        return "redirect:/doctor/dispense?success=dispensed";
    }

    @PostMapping("/dispense/reject/{id}")
    @Transactional
    public String rejectDoctorDispense(@PathVariable Long id,
                                       HttpSession session) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        Prescription pres = prescriptionRepository.findById(id).orElse(null);
        if (pres == null) {
            return "redirect:/doctor/dispense?error=notfound";
        }
        if (pres.getStatus() != PrescriptionStatus.PENDING) {
            return "redirect:/doctor/dispense?error=processed";
        }
        Appointment appt = pres.getMedicalRecord().getAppointment();
        if (appt.getDoctor() == null ||
                !appt.getDoctor().getId().equals(doctor.getId())) {
            return "redirect:/doctor/dispense?error=forbidden";
        }
        pres.setStatus(PrescriptionStatus.REJECTED);
        prescriptionRepository.save(pres);
        long remain = prescriptionRepository.countByMedicalRecord_IdAndStatus(
                pres.getMedicalRecord().getId(),
                PrescriptionStatus.PENDING
        );
        if (remain == 0) {
            appt.setStatus("COMPLETED");
            appointmentRepository.save(appt);
        }
        return "redirect:/doctor/dispense?success=rejected";
    }

    @GetMapping("/appointments/confirm/{id}")
    public String confirmAppointment(@PathVariable Long id,
                                     HttpSession session) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }
        appt.setStatus("CONFIRMED");
        appointmentRepository.save(appt);
        return "redirect:/doctor/appointments?success=confirmed";
    }

    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id,
                                    HttpSession session) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }
        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
        return "redirect:/doctor/appointments?success=cancelled";
    }

    @GetMapping("/appointments/examine/{id}")
    public String showExamineForm(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }
        MedicalRecord existingRecord =
                medicalRecordRepository.findByAppointment_Id(id)
                        .orElse(new MedicalRecord());
        model.addAttribute("appointment", appt);
        model.addAttribute("record", existingRecord);
        model.addAttribute("medicines", medicineRepository.findAll());
        return "doctor/examine";
    }

    @PostMapping("/appointments/examine/{id}/save")
    @Transactional
    public String saveMedicalRecord(@PathVariable Long id,
                                    @Valid @ModelAttribute("record") MedicalRecord record,
                                    BindingResult result,
                                    @RequestParam(required = false) List<String> medicineIds,
                                    @RequestParam(required = false) List<String> dosages,
                                    @RequestParam(required = false) List<String> instructions,
                                    HttpSession session,
                                    Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }
        if (result.hasErrors()) {
            model.addAttribute("appointment", appt);
            model.addAttribute("medicines", medicineRepository.findAll());
            return "doctor/examine";
        }
        List<Long> selectedMedicineIds = new ArrayList<>();
        List<Integer> selectedDosages = new ArrayList<>();
        List<String> selectedInstructions = new ArrayList<>();
        if (medicineIds != null && !medicineIds.isEmpty()) {
            for (int i = 0; i < medicineIds.size(); i++) {
                String medicineIdRaw = medicineIds.get(i);
                if (medicineIdRaw == null || medicineIdRaw.isBlank()) {
                    continue;
                }
                Long medicineId;
                try {
                    medicineId = Long.parseLong(medicineIdRaw);
                } catch (NumberFormatException ex) {
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    model.addAttribute("sysError", "Mã thuốc không hợp lệ.");
                    return "doctor/examine";
                }
                String dosageRaw =
                        (dosages != null && dosages.size() > i)
                                ? dosages.get(i)
                                : null;
                if (dosageRaw == null || dosageRaw.isBlank()) {
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    model.addAttribute("sysError",
                            "Vui lòng nhập đủ số lượng cho từng thuốc đã chọn.");
                    return "doctor/examine";
                }
                int dosage;
                try {
                    dosage = Integer.parseInt(dosageRaw);
                } catch (NumberFormatException ex) {
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    model.addAttribute("sysError",
                            "Liều lượng thuốc không hợp lệ.");
                    return "doctor/examine";
                }
                if (dosage < 1) {
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    model.addAttribute("sysError",
                            "Liều lượng thuốc phải lớn hơn 0.");
                    return "doctor/examine";
                }
                Medicine medicine =
                        medicineRepository.findById(medicineId).orElse(null);
                if (medicine == null) {
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    model.addAttribute("sysError", "Thuốc không tồn tại.");
                    return "doctor/examine";
                }
                if (medicine.getQuantity() < dosage) {
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    model.addAttribute(
                            "sysError",
                            "Thuốc " + medicine.getName()
                                    + " không đủ số lượng trong kho."
                    );
                    return "doctor/examine";
                }
                selectedMedicineIds.add(medicineId);
                selectedDosages.add(dosage);
                selectedInstructions.add(
                        (instructions != null && instructions.size() > i)
                                ? instructions.get(i)
                                : ""
                );
            }
        }
        MedicalRecord recordToSave =
                medicalRecordRepository.findByAppointment_Id(id)
                        .orElse(record);
        recordToSave.setAppointment(appt);
        recordToSave.setDiagnosis(record.getDiagnosis());
        recordToSave.setTreatmentPlan(record.getTreatmentPlan());
        recordToSave.setCreatedAt(LocalDateTime.now());
        if (recordToSave.getPrescriptions() != null) {
            recordToSave.getPrescriptions().clear();
        }
        medicalRecordRepository.save(recordToSave);
        if (!selectedMedicineIds.isEmpty()) {
            for (int i = 0; i < selectedMedicineIds.size(); i++) {
                Prescription p = new Prescription();
                p.setMedicalRecord(recordToSave);
                p.setMedicine(
                        medicineRepository.findById(selectedMedicineIds.get(i))
                                .orElseThrow()
                );
                p.setDosage(selectedDosages.get(i));
                p.setInstruction(selectedInstructions.get(i));
                p.setStatus(PrescriptionStatus.PENDING);
                prescriptionRepository.save(p);
            }
        }
        if (!selectedMedicineIds.isEmpty()) {
            appt.setStatus("WAITING_DISPENSE");
            appointmentRepository.save(appt);
            return "redirect:/doctor/dispense?success=fromExam";
        }
        appt.setStatus("COMPLETED");
        appointmentRepository.save(appt);
        return "redirect:/doctor/appointments?success=examined";
    }

    @GetMapping("/dispense/history")
    public String showDoctorDispenseHistory(HttpSession session,
                                            Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }
        var dispensedPrescriptions =
                prescriptionRepository.findByStatusInAndMedicalRecord_Appointment_Doctor(
                        List.of(
                                PrescriptionStatus.DISPENSED,
                                PrescriptionStatus.REJECTED
                        ),
                        doctor
                );
        model.addAttribute(
                "dispensedPrescriptions",
                dispensedPrescriptions
        );
        return "doctor/dispense-history";
    }
}