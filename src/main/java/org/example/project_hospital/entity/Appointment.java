package org.example.project_hospital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    @NotNull(message = "Vui lòng chọn ngày khám!")
    @FutureOrPresent(message = "Ngày khám không được nằm trong quá khứ!")
    private LocalDate appointmentDate;

    @Column(nullable = false)
    @NotBlank(message = "Vui lòng chọn giờ khám!")
    private String appointmentTime;

    @Column(length = 500)

    @NotBlank(message = "Vui lòng nhập triệu chứng để bác sĩ chuẩn bị!")
    @Size(min = 10, max = 500, message = "Triệu chứng nên mô tả rõ ràng (từ 10 đến 500 ký tự)!")
    private String reason;

    @Column(nullable = false)
    private String status = "PENDING";

    @Version
    private Long version;

}