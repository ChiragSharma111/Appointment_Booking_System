package com.projects.appointment_booking_system.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.appointment_booking_system.dto.AppointmentCreateRequest;
import com.projects.appointment_booking_system.dto.AppointmentResponse;
import com.projects.appointment_booking_system.service.AppointmentService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }
    
    @PostMapping 
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody @Valid  AppointmentCreateRequest request){
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    @PatchMapping ("/cancel/{id}")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PatchMapping ("/complete/{id}")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    @GetMapping ("/by-id/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping 
    public ResponseEntity<Page<AppointmentResponse>> findAll(@PageableDefault (size = 20, sort = "appointmentTime", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(appointmentService.viewAll(pageable));
    }

    @GetMapping ("/by-doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentResponse>> findByDoctor(@PathVariable Long doctorId, Pageable pageable){
        return ResponseEntity.ok(appointmentService.findByDoctor(doctorId, pageable));
    }
}
