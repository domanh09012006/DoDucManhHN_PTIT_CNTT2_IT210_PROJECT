package org.example.project_hospital.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {

    @NotBlank(message = "Vui lòng nhập tên đăng nhập!")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu!")
    private String password;

}