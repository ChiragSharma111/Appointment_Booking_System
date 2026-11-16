package com.projects.appointment_booking_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.appointment_booking_system.dto.DoctorCreateRequest;
import com.projects.appointment_booking_system.dto.DoctorResponse;
import com.projects.appointment_booking_system.dto.DoctorUpdateRequest;
import com.projects.appointment_booking_system.entity.Doctor;
import com.projects.appointment_booking_system.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    
    @Mock 
    private DoctorRepository doctorRepository;

    @InjectMocks 
    private DoctorService doctorService;

    @Test 
    void createDoctorTest(){
        DoctorCreateRequest request = new DoctorCreateRequest();
        request.setName("Chirag");
        request.setEmail("chiragsharma@gmail.com");
        request.setSpecialization("Physian");

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Chirag");
        doctor.setEmail("chiragsharma@gmail.com");
        doctor.setSpecialization("Physian");

        when(doctorRepository.save(any(Doctor.class)))
            .thenReturn(doctor);

        DoctorResponse response = doctorService.createDocter(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Chirag", response.getName());
        assertEquals("chiragsharma@gmail.com", response.getEmail());
        assertEquals("Physian", response.getSpecialization());

        verify(doctorRepository,times(1))
              .save(any(Doctor.class));
    }

    @Test 
    void findDoctorByIdTest(){
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Chirag");
        doctor.setEmail("chiragsharma@gmail.com");
        doctor.setSpecialization("Physian");
        
        when(doctorRepository.findById(1L))
            .thenReturn(Optional.of(doctor));

        DoctorResponse response = doctorService.findDoctorById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Chirag", response.getName());
        assertEquals("chiragsharma@gmail.com", response.getEmail());
        assertEquals("Physian", response.getSpecialization());

        verify(doctorRepository).findById(1L);
    }

    @Test 
    void updateDoctorTest(){
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Chirag");
        doctor.setEmail("chiragsharma@gmail.com");
        doctor.setSpecialization("Physian");

        DoctorUpdateRequest request = new DoctorUpdateRequest();
        request.setName("chirag");
        request.setEmail("chiragsharma123@gmail.com");
        request.setSpecialization("Physians");

        when(doctorRepository.findById(1L))
            .thenReturn(Optional.of(doctor));

        when(doctorRepository.save(doctor))
            .thenReturn(doctor);

        DoctorResponse response = doctorService.updateDoctor(1L, request);
        assertEquals(1L, response.getId());
        assertEquals("chirag", response.getName());
        assertEquals("chiragsharma123@gmail.com", response.getEmail());
        assertEquals("Physians", response.getSpecialization());
    }
}
