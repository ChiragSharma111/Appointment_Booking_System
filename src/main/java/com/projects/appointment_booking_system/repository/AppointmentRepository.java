package com.projects.appointment_booking_system.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.appointment_booking_system.entity.Appointment;
import com.projects.appointment_booking_system.entity.enums.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment,Long>{
    
    Optional<Appointment> findTopByDoctorIdAndStatusOrderByAppointmentTimeDesc(Long doctorId, AppointmentStatus Status);

    Page<Appointment> findAllByDoctorId(Long id, Pageable pageable);
}
