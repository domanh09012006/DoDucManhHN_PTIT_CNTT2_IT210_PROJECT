package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.project_hospital.dto.AuthDTO;
import org.example.project_hospital.dto.LoginDTO;
import org.example.project_hospital.entity.User;
import org.example.project_hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ================= ĐĂNG NHẬP =================

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                               org.springframework.validation.BindingResult bindingResult,
                               HttpSession session, Model model) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            return "login";
        }

        // Xác thực tài khoản
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

        if (user != null) {
            // Lưu thông tin user vào session
            session.setAttribute("loggedInUser", user);

            // Chuyển hướng theo vai trò
            switch (user.getRole()) {
                case ADMIN:
                    return "redirect:/admin/dashboard";
                case DOCTOR:
                    return "redirect:/doctor/appointments";
                case PATIENT:
                    return "redirect:/patient/home";
                default:
                    return "redirect:/login";
            }
        }

        // Đăng nhập thất bại
        model.addAttribute("error", "Sai tài khoản hoặc mật khẩu!");
        return "login";
    }

    // ================= ĐĂNG KÝ TÀI KHOẢN =================

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("authDTO", new AuthDTO());
        return "register";
    }

    // Xử lý đăng ký tài khoản mới (mặc định là PATIENT)
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("authDTO") AuthDTO authDTO,
                                  org.springframework.validation.BindingResult bindingResult,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Kiểm tra username và email đã tồn tại chưa
        if (userService.existsByUsername(authDTO.getUsername()) ||
                userService.existsByEmail(authDTO.getEmail())) {
            model.addAttribute("error", "Tên đăng nhập hoặc Email đã tồn tại!");
            return "register";
        }

        // Đăng ký thành công
        userService.register(authDTO);
        return "redirect:/login?registered=true";
    }

    // ================= ĐĂNG XUẤT =================

    // Đăng xuất và xóa session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}