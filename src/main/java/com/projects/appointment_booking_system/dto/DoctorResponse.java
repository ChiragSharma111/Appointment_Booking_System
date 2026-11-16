package com.projects.appointment_booking_system.dto;

import com.projects.appointment_booking_system.entity.Doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class DoctorResponse {
    
    private Long id;
    private String name;
    private String specialization;
    private String email;

    public static DoctorResponse fromEntity(Doctor doctor){
        return DoctorResponse.builder()
              .id(doctor.getId())
              .name(doctor.getName())
              .specialization(doctor.getSpecialization())
              .email(doctor.getEmail())
              .build();

    }
}
