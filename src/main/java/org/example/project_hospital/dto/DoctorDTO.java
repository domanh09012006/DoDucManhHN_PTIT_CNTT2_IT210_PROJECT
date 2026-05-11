package org.example.project_hospital.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorDTO {

    private Long id;

    @NotBlank(message = "Tên đăng nhập không được để trống!")
    @Size(min = 3, max = 20, message = "Tên đăng nhập phải từ 3-20 ký tự!")
    private String username;

    private String password;

    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không hợp lệ!")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống!")
    private String fullName;

    @NotNull(message = "Vui lòng chọn chuyên khoa!")
    private Long specialtyId;

    @Min(value = 1, message = "Kinh nghiệm tối thiểu phải là 1 năm!")
    @Max(value = 60, message = "Số năm kinh nghiệm không hợp lý!")
    private int experienceYears;
}

