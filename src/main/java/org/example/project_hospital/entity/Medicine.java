package org.example.project_hospital.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "medicines")
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Tên thuốc không được để trống!")
    private String name;

    private String manufacturer;

    @Min(value = 0, message = "Số lượng trong kho không được âm!")
    private int quantity;

    @DecimalMin(value = "1000.0", message = "Giá thuốc tối thiểu là 1,000đ!")
    private double price;

    private String description;

    @Version
    private Long version;
}