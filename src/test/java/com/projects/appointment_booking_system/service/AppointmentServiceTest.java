package com.projects.appointment_booking_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.projects.appointment_booking_system.dto.AppointmentCreateRequest;
import com.projects.appointment_booking_system.dto.AppointmentResponse;
import com.projects.appointment_booking_system.entity.Appointment;
import com.projects.appointment_booking_system.entity.Doctor;
import com.projects.appointment_booking_system.entity.enums.AppointmentStatus;
import com.projects.appointment_booking_system.exception.ResourceNotFoundException;
import com.projects.appointment_booking_system.repository.AppointmentRepository;
import com.projects.appointment_booking_system.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock 
    private AppointmentRepository appointmentRepository;

    @InjectMocks 
    private AppointmentService appointmentService;

    private Doctor sampleDoctor;
    private Appointment sampleAppointment;
    private AppointmentCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        sampleDoctor = Doctor.builder()
                .id(1L)
                .name("Dr. Chirag")
                .email("chirag@hospital.com")
                .specialization("Cardiology")
                .build();

        sampleAppointment = Appointment.builder()
                .id(1L)
                .patientName("Rahul")
                .patientEmail("rahul@gmail.com")
                .doctor(sampleDoctor)
                .appointmentTime(LocalDateTime.now().plusDays(1))
                .reason("Checkup")
                .status(AppointmentStatus.BOOKED)
                .build();

        createRequest = new AppointmentCreateRequest();
        createRequest.setPatientName("Rahul");
        createRequest.setPatientEmail("rahul@gmail.com");
        createRequest.setDoctorId(1L);
        createRequest.setReason("Checkup");
    }

    @Test 
    void createAppointment_Success_NoPreviousAppointments() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor));
        when(appointmentRepository.findTopByDoctorIdAndStatusOrderByAppointmentTimeDesc(1L, AppointmentStatus.BOOKED))
                .thenReturn(Optional.empty());

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentService.createAppointment(createRequest);

        assertNotNull(response);
        assertEquals("Rahul", response.getPatientName());
        assertEquals("rahul@gmail.com", response.getPatientEmail());
        assertEquals("Checkup", response.getReason());
        assertEquals(AppointmentStatus.BOOKED, response.getStatus());
        assertNotNull(response.getAllotedTime());

        verify(doctorRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_Success_WithPreviousAppointment_Calculates20MinLater() {
        LocalDateTime previousSlot = LocalDateTime.now().plusHours(2);
        Appointment previousAppointment = Appointment.builder()
                .id(99L)
                .doctor(sampleDoctor)
                .appointmentTime(previousSlot)
                .status(AppointmentStatus.BOOKED)
                .build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(sampleDoctor));
        when(appointmentRepository.findTopByDoctorIdAndStatusOrderByAppointmentTimeDesc(1L, AppointmentStatus.BOOKED))
                .thenReturn(Optional.of(previousAppointment));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentService.createAppointment(createRequest);

        assertNotNull(response);
        assertEquals(previousSlot.plusMinutes(20), response.getAllotedTime());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test 
    void createAppointment_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.createAppointment(createRequest);
        });

        verify(doctorRepository).findById(1L);
        verify(appointmentRepository, times(0)).save(any());
    }

    @Test 
    void cancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(sampleAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentService.cancelAppointment(1L);

        assertNotNull(response);
        assertEquals(AppointmentStatus.CANCELLED, response.getStatus());

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(sampleAppointment);
    }

    @Test 
    void cancelAppointment_NotFound_ThrowsException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.cancelAppointment(1L);
        });

        verify(appointmentRepository).findById(1L);
    }

    @Test 
    void getAppointmentById_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(sampleAppointment));

        AppointmentResponse response = appointmentService.getAppointmentById(1L);

        assertNotNull(response);
        assertEquals("Rahul", response.getPatientName());
        assertEquals("rahul@gmail.com", response.getPatientEmail());
        assertEquals(AppointmentStatus.BOOKED, response.getStatus());

        verify(appointmentRepository).findById(1L);
    }

    @Test 
    void getAppointmentById_NotFound_ThrowsException() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.getAppointmentById(1L);
        });

        verify(appointmentRepository).findById(1L);
    }

    @Test 
    void viewAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(sampleAppointment), pageable, 1);

        when(appointmentRepository.findAll(pageable)).thenReturn(page);

        Page<AppointmentResponse> result = appointmentService.viewAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Rahul", result.getContent().get(0).getPatientName());

        verify(appointmentRepository).findAll(pageable);
    }

    @Test 
    void findByDoctor_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> page = new PageImpl<>(List.of(sampleAppointment), pageable, 1);

        when(appointmentRepository.findAllByDoctorId(eq(1L), eq(pageable))).thenReturn(page);

        Page<AppointmentResponse> result = appointmentService.findByDoctor(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Rahul", result.getContent().get(0).getPatientName());

        verify(appointmentRepository).findAllByDoctorId(1L, pageable);
    }
}
