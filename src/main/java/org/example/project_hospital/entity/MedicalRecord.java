package org.example.project_hospital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @NotBlank(message = "Chẩn đoán không được để trống")
    private String diagnosis;

    private String treatmentPlan;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;
}
