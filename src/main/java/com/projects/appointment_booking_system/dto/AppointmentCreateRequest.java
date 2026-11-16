package com.projects.appointment_booking_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class AppointmentCreateRequest {
    
    @NotBlank (message = "Patient Name is required")
    @Size (max = 100, message = "Patient Name should not exceed 100 char")
    private String patientName;

    @Email (message = "Invalid Email format")
    @NotBlank (message = "Patient email is required")
    private String patientEmail;

    @NotNull (message = "Doctor id is required")
    private Long doctorId;

    @NotBlank (message = "Reason is required")
    @Size (max = 1000, message = "Reason is required")
    private String reason;
}
