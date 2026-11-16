package com.projects.appointment_booking_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.projects.appointment_booking_system.dto.DoctorCreateRequest;
import com.projects.appointment_booking_system.dto.DoctorResponse;
import com.projects.appointment_booking_system.dto.DoctorUpdateRequest;
import com.projects.appointment_booking_system.entity.Doctor;
import com.projects.appointment_booking_system.exception.ResourceNotFoundException;
import com.projects.appointment_booking_system.repository.DoctorRepository;

@Service 
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    public DoctorService(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponse createDocter(DoctorCreateRequest doctorCreateRequest){
        Doctor doctor = Doctor.builder()
                       .name(doctorCreateRequest.getName())
                       .email(doctorCreateRequest.getEmail())
                       .specialization(doctorCreateRequest.getSpecialization().toLowerCase())
                       .build();
        Doctor saved = doctorRepository.save(doctor);
        return DoctorResponse.fromEntity(saved);
    }

    public DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request){
        Doctor doctor = doctorRepository.findById(id)
                       .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialization(request.getSpecialization());
        doctorRepository.save(doctor);
        return DoctorResponse.fromEntity(doctor);
    }

    public Page<DoctorResponse> findDoctor(String specialization, Pageable pageable){
        Page<Doctor> doctors;
        if(specialization != null && !specialization.isBlank()){
            doctors = doctorRepository.findBySpecializationIgnoreCase(specialization, pageable);
        }
        else{
            doctors = doctorRepository.findAll(pageable);
        }
        return doctors.map(DoctorResponse :: fromEntity);
    }

    public DoctorResponse findDoctorById(Long id){
        Doctor doctor = doctorRepository.findById(id)
                       .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return DoctorResponse.fromEntity(doctor);
    }
}
