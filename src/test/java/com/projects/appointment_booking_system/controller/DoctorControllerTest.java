package com.projects.appointment_booking_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.projects.appointment_booking_system.dto.DoctorCreateRequest;
import com.projects.appointment_booking_system.dto.DoctorResponse;
import com.projects.appointment_booking_system.dto.DoctorUpdateRequest;
import com.projects.appointment_booking_system.service.DoctorService;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDoctorTest() throws Exception {

        DoctorCreateRequest request = new DoctorCreateRequest();
        request.setName("Chirag");
        request.setEmail("chiragsharma@gmail.com");
        request.setSpecialization("Physian");

        DoctorResponse response = DoctorResponse.builder()
                .id(1L)
                .name("Chirag")
                .email("chiragsharma@gmail.com")
                .specialization("Physian")
                .build();
        when(doctorService.createDocter(request))
                .thenReturn(response);

        mockMvc.perform(post("/doctors/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Chirag"))
                .andExpect(jsonPath("$.email").value("chiragsharma@gmail.com"))
                .andExpect(jsonPath("$.specialization").value("Physian"));

    }

    @Test
    void getByIdTest() throws Exception {
        DoctorResponse response = DoctorResponse.builder()
                .id(1L)
                .name("Chirag")
                .email("chiragsharma@gmail.com")
                .specialization("Physian")
                .build();

        when(doctorService.findDoctorById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Chirag"))
                .andExpect(jsonPath("$.email").value("chiragsharma@gmail.com"))
                .andExpect(jsonPath("$.specialization").value("Physian"));
    }

    @Test
    void updateDoctorTest() throws Exception {

        DoctorUpdateRequest request = new DoctorUpdateRequest();
        request.setName("Chirag");
        request.setEmail("chiragsharma@gmail.com");
        request.setSpecialization("Physian");

        DoctorResponse response = DoctorResponse.builder()
                .id(1L)
                .name("Chirag")
                .email("chiragsharma@gmail.com")
                .specialization("Physian")
                .build();

        when(doctorService.updateDoctor(eq(1L), any(DoctorUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/doctors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Chirag"))
                .andExpect(jsonPath("$.email").value("chiragsharma@gmail.com"))
                .andExpect(jsonPath("$.specialization").value("Physian"));
    }
}
