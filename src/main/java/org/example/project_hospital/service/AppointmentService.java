package org.example.project_hospital.service;

import org.example.project_hospital.entity.Appointment;
import org.example.project_hospital.entity.User;
import org.example.project_hospital.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPatient(User patient) {
        return appointmentRepository.findByPatient(patient);
    }
}