package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_hospital.entity.*;
import org.example.project_hospital.repository.DoctorRepository;
import org.example.project_hospital.repository.MedicalRecordRepository;
import org.example.project_hospital.repository.UserProfileRepository;
import org.example.project_hospital.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final UserProfileRepository userProfileRepository;
    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    // ================= TRANG CHỦ BỆNH NHÂN =================

    // Hiển thị trang chủ sau khi bệnh nhân đăng nhập
    @GetMapping("/home")
    public String showPatientHome(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";
        return "patient/home";
    }

    // ================= QUẢN LÝ HỒ SƠ CÁ NHÂN =================

    // Hiển thị thông tin hồ sơ bệnh nhân
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";

        UserProfile profile = userProfileRepository.findByUser(user).orElse(new UserProfile());
        model.addAttribute("profile", profile);
        model.addAttribute("userEmail", user.getEmail());
        return "patient/profile";
    }

    // Cập nhật thông tin hồ sơ cá nhân
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profile") UserProfile profileData,
                                BindingResult result,
                                Model model,
                                HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("profile", profileData);
            model.addAttribute("userEmail", user.getEmail());
            return "patient/profile";
        }

        UserProfile existingProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());
        existingProfile.setFullName(profileData.getFullName());
        existingProfile.setPhone(profileData.getPhone());
        existingProfile.setAddress(profileData.getAddress());
        existingProfile.setGender(profileData.getGender());
        existingProfile.setUser(user);

        userProfileRepository.save(existingProfile);
        return "redirect:/patient/profile?success=true";
    }

    // ================= ĐẶT LỊCH KHÁM =================

    // Hiển thị form đặt lịch khám và danh sách lịch đã đặt
    @GetMapping("/booking")
    public String showBookingForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";

        List<Doctor> doctors = doctorRepository.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("myAppointments", appointmentService.getAppointmentsByPatient(user));

        return "patient/booking";
    }

    // Xử lý đặt lịch khám mới
    @PostMapping("/booking")
    public String processBooking(@Valid @ModelAttribute("appointment") Appointment appointment,
                                 BindingResult result,
                                 Model model,
                                 HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";

        if (result.hasErrors()) {
            List<Doctor> doctors = doctorRepository.findAll();
            model.addAttribute("doctors", doctors);
            model.addAttribute("myAppointments", appointmentService.getAppointmentsByPatient(user));
            return "patient/booking";
        }

        appointment.setPatient(user);
        appointmentService.saveAppointment(appointment);
        return "redirect:/patient/booking?success=booked";
    }

    // Hủy lịch khám (chỉ cho phép hủy khi chưa được xác nhận)
    @GetMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";

        Appointment appt = appointmentService.getAppointmentsByPatient(user).stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (appt == null)
            return "redirect:/patient/booking?error=notfound";

        if (!"PENDING".equals(appt.getStatus())) {
            return "redirect:/patient/booking?error=notAllowed";
        }

        appt.setStatus("CANCELLED");
        appointmentService.saveAppointment(appt);
        return "redirect:/patient/booking?success=cancelled";
    }

    // ================= LỊCH SỬ KHÁM BỆNH =================

    // Hiển thị danh sách lịch sử khám bệnh của bệnh nhân
    @GetMapping("/history")
    public String showMedicalHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";

        List<MedicalRecord> rawRecords = medicalRecordRepository.findHistoryByPatient(user);
        model.addAttribute("medicalRecords", filterVisibleHistory(rawRecords));
        return "patient/history";
    }

    // Xem chi tiết một lần khám bệnh
    @GetMapping("/history/{id}")
    public String showMedicalHistoryDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.PATIENT)
            return "redirect:/login";

        var recordOpt = medicalRecordRepository.findHistoryDetailByIdAndPatient(id, user);
        if (recordOpt.isEmpty() || !isVisibleHistory(recordOpt.get())) {
            return "redirect:/patient/history?error=notfound";
        }

        model.addAttribute("record", recordOpt.get());
        return "patient/history-detail";
    }

    // Lọc chỉ hiển thị những hồ sơ đã hoàn tất cấp phát thuốc (không còn PENDING)
    private List<MedicalRecord> filterVisibleHistory(List<MedicalRecord> records) {
        return records.stream().filter(this::isVisibleHistory).toList();
    }

    // Kiểm tra hồ sơ có được hiển thị hay không
    private boolean isVisibleHistory(MedicalRecord record) {
        if (record.getPrescriptions() == null || record.getPrescriptions().isEmpty()) {
            return true;
        }
        boolean hasPending = record.getPrescriptions().stream()
                .anyMatch(p -> p.getStatus() == PrescriptionStatus.PENDING);
        return !hasPending;   // Chỉ hiển thị khi tất cả đơn thuốc đã được cấp phát hoặc từ chối
    }
}