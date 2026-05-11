package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_hospital.dto.DoctorDTO;
import org.example.project_hospital.entity.*;
import org.example.project_hospital.repository.*;
import org.example.project_hospital.service.DispenseService;
import org.example.project_hospital.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final PrescriptionRepository prescriptionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && user.getRole() == Role.ADMIN;
    }

    // ================= 1. DASHBOARD =================
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("totalMedicines", medicineRepository.count());
        model.addAttribute("totalPatients", userRepository.findByRole(Role.PATIENT).size());
        model.addAttribute("totalAppointments", appointmentRepository.count());

        return "admin/dashboard";
    }

    // ================= 2. QUẢN LÝ BỆNH NHÂN =================
    @GetMapping("/users")
    public String managePatients(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("patients", userRepository.findByRole(Role.PATIENT));
        return "admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        userRepository.deleteById(id);
        return "redirect:/admin/users?success=deleted";
    }

    // ================= 3. QUẢN LÝ BÁC SĨ =================
    @GetMapping("/doctors")
    public String manageDoctors(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("specialties", specialtyRepository.findAll());
        model.addAttribute("newDoctor", new DoctorDTO());
        return "admin/doctors";
    }

    @PostMapping("/doctors/save")
    @Transactional
    public String saveDoctor(@Valid @ModelAttribute("newDoctor") DoctorDTO doctorDTO,
                            BindingResult result,
                            Model model,
                            HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("specialties", specialtyRepository.findAll());
            return "admin/doctors";
        }

        if (userRepository.existsByUsername(doctorDTO.getUsername())) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("specialties", specialtyRepository.findAll());
            model.addAttribute("error", "Tên đăng nhập '" + doctorDTO.getUsername() + "' đã tồn tại!");
            return "admin/doctors";
        }

        if (userRepository.existsByEmail(doctorDTO.getEmail())) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("specialties", specialtyRepository.findAll());
            model.addAttribute("error", "Email '" + doctorDTO.getEmail() + "' đã được sử dụng!");
            return "admin/doctors";
        }

        if (doctorDTO.getPassword() == null || doctorDTO.getPassword().isBlank() || doctorDTO.getPassword().length() < 6) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("specialties", specialtyRepository.findAll());
            model.addAttribute("error", "Mật khẩu phải ít nhất 6 ký tự!");
            return "admin/doctors";
        }

        var specialtyOpt = specialtyRepository.findById(doctorDTO.getSpecialtyId());
        if (specialtyOpt.isEmpty()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("specialties", specialtyRepository.findAll());
            model.addAttribute("error", "Chuyên khoa không tồn tại hoặc chưa được chọn!");
            return "admin/doctors";
        }

        User newUser = new User();
        newUser.setUsername(doctorDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        newUser.setEmail(doctorDTO.getEmail());
        newUser.setRole(Role.DOCTOR);
        User savedUser = userRepository.save(newUser);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setFullName(doctorDTO.getFullName());
        doctor.setSpecialty(specialtyOpt.get());
        doctor.setExperienceYears(doctorDTO.getExperienceYears());
        doctorRepository.save(doctor);

        return "redirect:/admin/doctors?success=created";
    }

    @GetMapping("/doctors/delete/{id}")
    @Transactional
    public String deleteDoctor(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        Doctor doctor = doctorRepository.findById(id).orElseThrow();
        User user = doctor.getUser();
        doctorRepository.deleteById(id);
        userRepository.deleteById(user.getId());

        return "redirect:/admin/doctors?success=deleted";
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctor(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        Doctor doctor = doctorRepository.findById(id).orElseThrow();
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setUsername(doctor.getUser().getUsername());
        doctorDTO.setEmail(doctor.getUser().getEmail());
        doctorDTO.setFullName(doctor.getFullName());
        doctorDTO.setSpecialtyId(doctor.getSpecialty().getId());
        doctorDTO.setExperienceYears(doctor.getExperienceYears());

        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("specialties", specialtyRepository.findAll());
        model.addAttribute("newDoctor", doctorDTO);
        return "admin/doctors";
    }

    @PostMapping("/doctors/update")
    @Transactional
    public String updateDoctor(@Valid @ModelAttribute("newDoctor") DoctorDTO doctorDTO,
                              BindingResult result,
                              Model model,
                              HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("specialties", specialtyRepository.findAll());
            return "admin/doctors";
        }

        Doctor doctor = doctorRepository.findById(doctorDTO.getId()).orElseThrow();
        User user = doctor.getUser();

        doctor.setFullName(doctorDTO.getFullName());
        doctor.setSpecialty(specialtyRepository.findById(doctorDTO.getSpecialtyId()).orElseThrow());
        doctor.setExperienceYears(doctorDTO.getExperienceYears());
        doctorRepository.save(doctor);

        if (!user.getEmail().equals(doctorDTO.getEmail())) {
            if (userRepository.existsByEmail(doctorDTO.getEmail())) {
                model.addAttribute("doctors", doctorRepository.findAll());
                model.addAttribute("specialties", specialtyRepository.findAll());
                model.addAttribute("error", "Email '" + doctorDTO.getEmail() + "' đã được sử dụng!");
                return "admin/doctors";
            }
            user.setEmail(doctorDTO.getEmail());
            userRepository.save(user);
        }

        if (doctorDTO.getPassword() != null && !doctorDTO.getPassword().isBlank()) {
            if (doctorDTO.getPassword().length() < 6) {
                model.addAttribute("doctors", doctorRepository.findAll());
                model.addAttribute("specialties", specialtyRepository.findAll());
                model.addAttribute("error", "Mật khẩu mới phải ít nhất 6 ký tự!");
                return "admin/doctors";
            }
            user.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
            userRepository.save(user);
        }

        return "redirect:/admin/doctors?success=updated";
    }

    // ================= 4. QUẢN LÝ KHO THUỐC =================
    @GetMapping("/medicines")
    public String manageMedicines(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("medicines", medicineRepository.findAll());
        model.addAttribute("newMedicine", new Medicine());
        return "admin/medicines";
    }

    @GetMapping("/medicines/edit/{id}")
    public String editMedicine(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        Medicine medicine = medicineRepository.findById(id).orElseThrow();
        model.addAttribute("medicines", medicineRepository.findAll());
        model.addAttribute("newMedicine", medicine);
        return "admin/medicines";
    }

    @PostMapping("/medicines/save")
    public String saveMedicine(@Valid @ModelAttribute("newMedicine") Medicine medicine,
                               BindingResult result, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("medicines", medicineRepository.findAll());
            return "admin/medicines";
        }

        medicineRepository.save(medicine);
        return "redirect:/admin/medicines?success=saved";
    }

    // ================= 6. CẤP PHÁT THUỐC (PHARMACY) =================
    private final DispenseService dispenseService;

    @GetMapping("/dispense")
    public String manageDispense(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("pendingPrescriptions",
                prescriptionRepository.findByStatus(PrescriptionStatus.PENDING));
        model.addAttribute("activePage", "dispense");
        return "admin/dispense";
    }

    @PostMapping("/dispense/confirm/{id}")
    public String confirmDispense(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null) return "redirect:/login";

        String result = dispenseService.confirmDispense(id, admin);

        if ("stock".equals(result)) {
            return "redirect:/admin/dispense?error=stock";
        } else if ("processed".equals(result)) {
            return "redirect:/admin/dispense?error=processed";
        } else if ("dispensed".equals(result)) {
            return "redirect:/admin/dispense?success=dispensed";
        } else {
            return "redirect:/admin/dispense?error=unknown";
        }
    }

    @PostMapping("/dispense/reject/{id}")
    public String rejectDispense(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null) return "redirect:/login";

        String result = dispenseService.rejectDispense(id, admin);

        if ("processed".equals(result)) {
            return "redirect:/admin/dispense?error=processed";
        } else if ("rejected".equals(result)) {
            return "redirect:/admin/dispense?success=rejected";
        } else {
            return "redirect:/admin/dispense?error=unknown";
        }
    }

    // ================= 5. QUẢN LÝ CHUYÊN KHOA =================
    @GetMapping("/specialties")
    public String manageSpecialties(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("specialties", specialtyRepository.findAll());
        model.addAttribute("newSpecialty", new Specialty());
        return "admin/specialties";
    }

    @GetMapping("/specialties/delete/{id}")
    @Transactional
    public String deleteSpecialty(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        if (!specialtyRepository.existsById(id)) {
            return "redirect:/admin/specialties?error=notfound";
        }

        if (doctorRepository.existsBySpecialty_Id(id)) {
            return "redirect:/admin/specialties?error=hasDoctor";
        }

        specialtyRepository.deleteById(id);
        return "redirect:/admin/specialties?success=deleted";
    }

    @PostMapping("/specialties/save")
    public String saveSpecialty(@Valid @ModelAttribute("newSpecialty") Specialty specialty,
                                BindingResult result, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        if (result.hasErrors()) {
            model.addAttribute("specialties", specialtyRepository.findAll());
            return "admin/specialties";
        }

        specialtyRepository.save(specialty);
        return "redirect:/admin/specialties?success=saved";
    }

    // ================= 7. LỊCH SỬ CẤP PHÁT THUỐC =================
    @GetMapping("/dispense/history")
    public String manageDispenseHistory(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        var dispensedPrescriptions = prescriptionRepository.findByStatusIn(
                java.util.List.of(
                        org.example.project_hospital.entity.PrescriptionStatus.DISPENSED,
                        org.example.project_hospital.entity.PrescriptionStatus.REJECTED
                )
        );
        model.addAttribute("dispensedPrescriptions", dispensedPrescriptions);
        model.addAttribute("activePage", "dispense");
        return "admin/dispense-history";
    }
}