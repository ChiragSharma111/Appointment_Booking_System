package com.projects.appointment_booking_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.appointment_booking_system.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    Page<Doctor> findBySpecializationIgnoreCase(String specialization, Pageable pageable);
}
