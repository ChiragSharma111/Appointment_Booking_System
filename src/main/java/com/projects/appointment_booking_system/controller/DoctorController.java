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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.appointment_booking_system.dto.DoctorCreateRequest;
import com.projects.appointment_booking_system.dto.DoctorResponse;
import com.projects.appointment_booking_system.dto.DoctorUpdateRequest;
import com.projects.appointment_booking_system.service.DoctorService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @PostMapping ("/create")
    public ResponseEntity<DoctorResponse> createDoctor(@RequestBody DoctorCreateRequest request){
        DoctorResponse response = doctorService.createDocter(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id){
        DoctorResponse response = doctorService.findDoctorById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable Long id, @RequestBody @Valid DoctorUpdateRequest request){
        DoctorResponse response = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/get-all")
    public ResponseEntity<Page<DoctorResponse>> findDoctor(@RequestParam (required = false) String specialization,@PageableDefault (size = 20, sort = "specialization", direction = Sort.Direction.ASC ) Pageable pageable){
        Page<DoctorResponse> page = doctorService.findDoctor(specialization, pageable);
        return ResponseEntity.ok(page);
    }
}
