package com.projects.appointment_booking_system.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projects.appointment_booking_system.dto.AppointmentCreateRequest;
import com.projects.appointment_booking_system.dto.AppointmentResponse;
import com.projects.appointment_booking_system.entity.Appointment;
import com.projects.appointment_booking_system.entity.Doctor;
import com.projects.appointment_booking_system.entity.enums.AppointmentStatus;
import com.projects.appointment_booking_system.exception.ResourceNotFoundException;
import com.projects.appointment_booking_system.repository.AppointmentRepository;
import com.projects.appointment_booking_system.repository.DoctorRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmetnRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.appointmetnRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Not Found with id: " + request.getDoctorId()));
        LocalDateTime slot = calculateNextSlot(request.getDoctorId());
        Appointment appointment = Appointment.builder()
                .patientName(request.getPatientName())
                .patientEmail(request.getPatientEmail())
                .reason(request.getReason())
                .doctor(doctor)
                .appointmentTime(slot)
                .build();

        Appointment saved = appointmetnRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    public LocalDateTime calculateNextSlot(Long doctorId) {
        Optional<Appointment> lastAppointment = appointmetnRepository
                .findTopByDoctorIdAndStatusOrderByAppointmentTimeDesc(doctorId, AppointmentStatus.BOOKED);

        if (lastAppointment.isPresent()) {
            LocalDateTime lastTime = lastAppointment.get().getAppointmentTime();

            if (lastTime.isBefore(LocalDateTime.now())) {
                return LocalDateTime.now().plusMinutes(20);
            }
            return lastTime.plusMinutes(20);
        }
        return LocalDateTime.now().plusMinutes(20);
    }

    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = appointmetnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        appointment.getStatus().validateTransitionTo(AppointmentStatus.CANCELLED);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmetnRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = appointmetnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointment.getStatus().validateTransitionTo(AppointmentStatus.COMPLETED);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment saved = appointmetnRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmetnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return AppointmentResponse.fromEntity(appointment);
    }

    public Page<AppointmentResponse> viewAll(Pageable pageable) {
        Page<Appointment> appointments = appointmetnRepository.findAll(pageable);
        return appointments.map(AppointmentResponse::fromEntity);
    }

    public Page<AppointmentResponse> findByDoctor(Long id, Pageable pageable) {
        Page<Appointment> appoinments = appointmetnRepository.findAllByDoctorId(id, pageable);
        return appoinments.map(AppointmentResponse::fromEntity);
    }
}
