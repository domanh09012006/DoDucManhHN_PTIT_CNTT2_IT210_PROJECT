package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.stereotype.Controller;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class})
    public String handleOptimisticLock(Exception ex, HttpServletRequest request) {
        return redirectToSafePage(request, "conflict");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return redirectToSafePage(request, "saveFailed");
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // In lỗi ra console
        return redirectToSafePage(request, "unknown");
    }

    private String redirectToSafePage(HttpServletRequest request, String errorCode) {
        String uri = request.getRequestURI();

        // Xử lý cho Dispense của Doctor
        if (uri != null && uri.contains("/doctor/dispense")) {
            return "redirect:/doctor/dispense?error=" + errorCode;
        }

        // Xử lý cho các trang khác
        if (uri != null && uri.startsWith("/doctor")) {
            return "redirect:/doctor/appointments?error=" + errorCode;
        }
        if (uri != null && uri.startsWith("/patient/profile")) {
            return "redirect:/patient/profile?error=" + errorCode;
        }
        if (uri != null && uri.startsWith("/patient/booking")) {
            return "redirect:/patient/booking?error=" + errorCode;
        }

        return "error";
    }
}