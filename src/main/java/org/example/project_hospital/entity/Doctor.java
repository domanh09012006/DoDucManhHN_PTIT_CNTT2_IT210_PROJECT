package org.example.project_hospital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    @NotBlank(message = "Họ tên bác sĩ không được để trống!")
    private String fullName;

    @NotNull(message = "Chuyên khoa không được để trống!")
    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Min(value = 1, message = "Kinh nghiệm tối thiểu phải là 1 năm!")
    @Max(value = 60, message = "Số năm kinh nghiệm không hợp lý!")
    private int experienceYears;

}