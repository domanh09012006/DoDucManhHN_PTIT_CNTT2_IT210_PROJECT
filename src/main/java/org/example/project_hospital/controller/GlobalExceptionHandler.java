package org.example.project_hospital.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    private String redirectToSafePage(HttpServletRequest request, String errorCode) {
        String uri = request.getRequestURI();
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



