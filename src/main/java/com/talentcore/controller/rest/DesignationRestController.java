package com.talentcore.controller.rest;

import com.talentcore.dto.request.DesignationRequest;
import com.talentcore.dto.response.ApiResponse;
import com.talentcore.dto.response.DesignationResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.service.DesignationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/designations")
@RequiredArgsConstructor
public class DesignationRestController {

    private final DesignationService designationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DesignationResponse>>> getAllDesignations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "active") String status) {
        return ResponseEntity.ok(ApiResponse.success("Designations retrieved",
                designationService.getAllDesignations(page, size, search, companyId, departmentId, status)));
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<ApiResponse<List<DesignationResponse>>> getDesignationsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success("Designations fetched",
                designationService.getActiveDesignationsByDepartment(departmentId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignationResponse>> getDesignationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Designation retrieved", designationService.getDesignationById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DesignationResponse>> createDesignation(@Valid @RequestBody DesignationRequest request) {
        DesignationResponse response = designationService.createDesignation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Designation created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignationResponse>> updateDesignation(
            @PathVariable Long id,
            @Valid @RequestBody DesignationRequest request) {
        DesignationResponse response = designationService.updateDesignation(id, request);
        return ResponseEntity.ok(ApiResponse.success("Designation updated successfully", response));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        designationService.toggleDesignationStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(active ? "Designation activated" : "Designation deactivated"));
    }
}
