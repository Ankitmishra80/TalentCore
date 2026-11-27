package com.talentcore.controller.rest;

import com.talentcore.dto.request.DepartmentRequest;
import com.talentcore.dto.response.ApiResponse;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DepartmentResponse>>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "active") String status) {
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved",
                departmentService.getAllDepartments(page, size, search, companyId, status)));
    }

    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.success("Departments fetched",
                departmentService.getActiveDepartmentsByCompany(companyId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Department retrieved", departmentService.getDepartmentById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Department created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", response));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        departmentService.toggleDepartmentStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(active ? "Department activated" : "Department deactivated"));
    }
}
