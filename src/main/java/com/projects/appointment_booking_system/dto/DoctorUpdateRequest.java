package com.projects.appointment_booking_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class DoctorUpdateRequest {

    @NotBlank (message = "Name is required")
    @Size (max = 100, message = "Name should not exceed 100 char")
    private String name;

    @NotBlank (message = "Specialization is required")
    @Size (max = 100, message = "Specialization should not exceed 100 char")
    private String specialization;

    @NotBlank (message = "Email is required")
    @Email (message = "Invalid email format") 
    private String email;

}
