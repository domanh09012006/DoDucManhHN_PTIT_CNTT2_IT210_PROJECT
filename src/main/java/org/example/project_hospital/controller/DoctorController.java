package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_hospital.entity.*;
import org.example.project_hospital.repository.*;
import org.example.project_hospital.service.DispenseService;
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
    private final DispenseService dispenseService;

    // ================= LẤY THÔNG TIN BÁC SĨ ĐANG ĐĂNG NHẬP =================

    // Lấy User của bác sĩ từ session
    private Optional<User> getLoggedInDoctorUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.DOCTOR) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    // Lấy thông tin Doctor đầy đủ từ User
    private Doctor getLoggedInDoctor(HttpSession session) {
        Optional<User> userOpt = getLoggedInDoctorUser(session);
        if (userOpt.isEmpty()) {
            return null;
        }
        return doctorRepository.findByUser(userOpt.get()).orElse(null);
    }

    // Kiểm tra lịch hẹn có thuộc về bác sĩ đang đăng nhập không
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

    // ================= 1. QUẢN LÝ LỊCH HẸN =================

    // Hiển thị danh sách lịch hẹn của bác sĩ
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

    // Xác nhận lịch hẹn
    @GetMapping("/appointments/confirm/{id}")
    public String confirmAppointment(@PathVariable Long id, HttpSession session) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) return "redirect:/login";

        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }
        appt.setStatus("CONFIRMED");
        appointmentRepository.save(appt);
        return "redirect:/doctor/appointments?success=confirmed";
    }

    // Hủy lịch hẹn
    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) return "redirect:/login";

        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }
        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
        return "redirect:/doctor/appointments?success=cancelled";
    }

    // ================= 2. KHÁM BỆNH & KÊ ĐƠN =================

    // Hiển thị form khám bệnh và kê đơn
    @GetMapping("/appointments/examine/{id}")
    public String showExamineForm(@PathVariable Long id,
                                  HttpSession session,
                                  Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) return "redirect:/login";

        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }

        MedicalRecord existingRecord = medicalRecordRepository.findByAppointment_Id(id)
                .orElse(new MedicalRecord());

        model.addAttribute("appointment", appt);
        model.addAttribute("record", existingRecord);
        model.addAttribute("medicines", medicineRepository.findAll());

        return "doctor/examine";
    }

    // Lưu bệnh án và đơn thuốc
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
        if (doctor == null) return "redirect:/login";

        Appointment appt = getDoctorAppointment(id, doctor);
        if (appt == null) {
            return "redirect:/doctor/appointments?error=forbidden";
        }

        if (result.hasErrors()) {
            model.addAttribute("appointment", appt);
            model.addAttribute("medicines", medicineRepository.findAll());
            return "doctor/examine";
        }

        List<Prescription> newPrescriptions = new ArrayList<>();

        // Xử lý kê đơn thuốc
        if (medicineIds != null && !medicineIds.isEmpty()) {
            for (int i = 0; i < medicineIds.size(); i++) {
                String medIdStr = medicineIds.get(i);
                if (medIdStr == null || medIdStr.trim().isEmpty()) continue;

                try {
                    Long medicineId = Long.parseLong(medIdStr.trim());
                    String dosageStr = (dosages != null && i < dosages.size()) ? dosages.get(i) : null;

                    if (dosageStr == null || dosageStr.trim().isEmpty()) {
                        model.addAttribute("sysError", "Vui lòng nhập liều lượng cho tất cả thuốc đã chọn.");
                        model.addAttribute("appointment", appt);
                        model.addAttribute("medicines", medicineRepository.findAll());
                        return "doctor/examine";
                    }

                    int dosage = Integer.parseInt(dosageStr.trim());
                    if (dosage < 1) {
                        throw new IllegalArgumentException("Liều lượng phải lớn hơn 0");
                    }

                    Medicine medicine = medicineRepository.findById(medicineId).orElse(null);
                    if (medicine == null) {
                        model.addAttribute("sysError", "Thuốc không tồn tại.");
                        model.addAttribute("appointment", appt);
                        model.addAttribute("medicines", medicineRepository.findAll());
                        return "doctor/examine";
                    }

                    Prescription p = new Prescription();
                    p.setMedicine(medicine);
                    p.setDosage(dosage);
                    p.setInstruction((instructions != null && i < instructions.size())
                            ? instructions.get(i) : "");
                    p.setStatus(PrescriptionStatus.PENDING);
                    newPrescriptions.add(p);

                } catch (Exception e) {
                    model.addAttribute("sysError", "Lỗi dữ liệu đơn thuốc: " + e.getMessage());
                    model.addAttribute("appointment", appt);
                    model.addAttribute("medicines", medicineRepository.findAll());
                    return "doctor/examine";
                }
            }
        }

        // Lưu bệnh án
        MedicalRecord recordToSave = medicalRecordRepository.findByAppointment_Id(id)
                .orElse(new MedicalRecord());

        recordToSave.setAppointment(appt);
        recordToSave.setDiagnosis(record.getDiagnosis());
        recordToSave.setTreatmentPlan(record.getTreatmentPlan());
        recordToSave.setCreatedAt(LocalDateTime.now());

        if (recordToSave.getPrescriptions() != null) {
            recordToSave.getPrescriptions().clear();
        }

        MedicalRecord savedRecord = medicalRecordRepository.save(recordToSave);

        // Lưu đơn thuốc
        if (!newPrescriptions.isEmpty()) {
            for (Prescription p : newPrescriptions) {
                p.setMedicalRecord(savedRecord);
                prescriptionRepository.save(p);
            }
            appt.setStatus("WAITING_DISPENSE");
        } else {
            appt.setStatus("COMPLETED");
        }

        appointmentRepository.save(appt);

        if (!newPrescriptions.isEmpty()) {
            return "redirect:/doctor/dispense?success=fromExam";
        } else {
            return "redirect:/doctor/appointments?success=examined";
        }
    }

    // ================= 3. CẤP PHÁT THUỐC =================

    // Hiển thị danh sách đơn thuốc chờ cấp phát
    @GetMapping("/dispense")
    public String showDoctorDispense(HttpSession session, Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) return "redirect:/login";

        model.addAttribute("pendingPrescriptions",
                prescriptionRepository.findByStatusAndMedicalRecord_Appointment_Doctor(
                        PrescriptionStatus.PENDING, doctor));
        return "doctor/dispense";
    }

    // Xác nhận cấp phát thuốc
    @PostMapping("/dispense/confirm/{id}")
    public String confirmDoctorDispense(@PathVariable Long id, HttpSession session) {
        User doctorUser = getLoggedInDoctorUser(session).orElse(null);
        if (doctorUser == null) return "redirect:/login";

        String result = dispenseService.confirmDispense(id, doctorUser);

        if ("stock".equals(result)) {
            return "redirect:/doctor/dispense?error=stock";
        } else if ("processed".equals(result)) {
            return "redirect:/doctor/dispense?error=processed";
        } else if ("forbidden".equals(result)) {
            return "redirect:/doctor/dispense?error=forbidden";
        } else if ("dispensed".equals(result)) {
            return "redirect:/doctor/dispense?success=dispensed";
        } else {
            return "redirect:/doctor/dispense?error=unknown";
        }
    }

    // Từ chối cấp phát thuốc
    @PostMapping("/dispense/reject/{id}")
    public String rejectDoctorDispense(@PathVariable Long id, HttpSession session) {
        User doctorUser = getLoggedInDoctorUser(session).orElse(null);
        if (doctorUser == null) return "redirect:/login";

        String result = dispenseService.rejectDispense(id, doctorUser);

        if ("processed".equals(result)) {
            return "redirect:/doctor/dispense?error=processed";
        } else if ("forbidden".equals(result)) {
            return "redirect:/doctor/dispense?error=forbidden";
        } else if ("rejected".equals(result)) {
            return "redirect:/doctor/dispense?success=rejected";
        } else {
            return "redirect:/doctor/dispense?error=unknown";
        }
    }

    // ================= 4. LỊCH SỬ CẤP PHÁT =================

    // Xem lịch sử cấp phát / từ chối thuốc của bác sĩ
    @GetMapping("/dispense/history")
    public String showDoctorDispenseHistory(HttpSession session, Model model) {
        Doctor doctor = getLoggedInDoctor(session);
        if (doctor == null) {
            return "redirect:/login";
        }

        var dispensedPrescriptions = prescriptionRepository
                .findByStatusInAndMedicalRecord_Appointment_Doctor(
                        List.of(PrescriptionStatus.DISPENSED, PrescriptionStatus.REJECTED),
                        doctor);

        model.addAttribute("dispensedPrescriptions", dispensedPrescriptions);
        return "doctor/dispense-history";
    }
}