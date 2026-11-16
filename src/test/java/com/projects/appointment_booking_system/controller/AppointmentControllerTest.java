package com.projects.appointment_booking_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.appointment_booking_system.dto.AppointmentCreateRequest;
import com.projects.appointment_booking_system.dto.AppointmentResponse;
import com.projects.appointment_booking_system.entity.enums.AppointmentStatus;
import com.projects.appointment_booking_system.service.AppointmentService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private AppointmentService appointmentService;

    @Autowired 
    private ObjectMapper objectMapper;

    private AppointmentResponse sampleResponse;
    private AppointmentCreateRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new AppointmentCreateRequest();
        sampleRequest.setPatientName("Rahul Sharma");
        sampleRequest.setPatientEmail("rahul@gmail.com");
        sampleRequest.setDoctorId(1L);
        sampleRequest.setReason("General Checkup");

        sampleResponse = AppointmentResponse.builder()
                .patientName("Rahul Sharma")
                .patientEmail("rahul@gmail.com")
                .reason("General Checkup")
                .status(AppointmentStatus.BOOKED)
                .allotedTime(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void createAppointmentTest() throws Exception {
        when(appointmentService.createAppointment(any(AppointmentCreateRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value("Rahul Sharma"))
                .andExpect(jsonPath("$.patientEmail").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.reason").value("General Checkup"))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void cancelAppointmentTest() throws Exception {
        AppointmentResponse cancelledResponse = AppointmentResponse.builder()
                .patientName("Rahul Sharma")
                .patientEmail("rahul@gmail.com")
                .reason("General Checkup")
                .status(AppointmentStatus.CANCELLED)
                .allotedTime(sampleResponse.getAllotedTime())
                .build();

        when(appointmentService.cancelAppointment(1L))
                .thenReturn(cancelledResponse);

        mockMvc.perform(patch("/appointments/cancel/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void completeAppointmentTest() throws Exception {
        AppointmentResponse completedResponse = AppointmentResponse.builder()
                .patientName("Rahul Sharma")
                .patientEmail("rahul@gmail.com")
                .reason("General Checkup")
                .status(AppointmentStatus.COMPLETED)
                .allotedTime(sampleResponse.getAllotedTime())
                .build();

        when(appointmentService.completeAppointment(1L))
                .thenReturn(completedResponse);

        mockMvc.perform(patch("/appointments/complete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getAppointmentByIdTest() throws Exception {
        when(appointmentService.getAppointmentById(1L))
                .thenReturn(sampleResponse);

        mockMvc.perform(get("/appointments/by-id/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value("Rahul Sharma"))
                .andExpect(jsonPath("$.patientEmail").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void findAllTest() throws Exception {
        Page<AppointmentResponse> page = new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1);

        when(appointmentService.viewAll(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].patientName").value("Rahul Sharma"))
                .andExpect(jsonPath("$.content[0].status").value("BOOKED"));
    }

    @Test
    void findByDoctorTest() throws Exception {
        Page<AppointmentResponse> page = new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1);

        when(appointmentService.findByDoctor(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/appointments/by-doctor/{doctorId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].patientName").value("Rahul Sharma"))
                .andExpect(jsonPath("$.content[0].status").value("BOOKED"));
    }
}
