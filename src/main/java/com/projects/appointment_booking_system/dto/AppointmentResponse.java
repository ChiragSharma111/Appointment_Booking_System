package com.projects.appointment_booking_system.dto;

import java.time.LocalDateTime;

import com.projects.appointment_booking_system.entity.Appointment;
import com.projects.appointment_booking_system.entity.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class AppointmentResponse {
    
    private String patientName;
    private String patientEmail;
    private String reason;
    private AppointmentStatus status;
    private LocalDateTime allotedTime;
    
    public static AppointmentResponse fromEntity(Appointment appointment){
        return AppointmentResponse.builder()
              .patientName(appointment.getPatientName())
              .patientEmail(appointment.getPatientEmail())
              .reason(appointment.getReason())
              .status(appointment.getStatus())
              .allotedTime(appointment.getAppointmentTime())
              .build();
    }
}
