package com.talentcore.controller.rest;

import com.talentcore.dto.request.BranchCreateRequest;
import com.talentcore.dto.request.BranchUpdateRequest;
import com.talentcore.dto.response.ApiResponse;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BranchRestController {

    private final BranchService branchService;

    /**
     * Preserved legacy endpoint: POST /api/v1/addBranch
     */
    @PostMapping("/api/v1/addBranch")
    public ResponseEntity<Map<String, Object>> legacyAddBranch(@Valid @RequestBody BranchCreateRequest request) {
        BranchResponse response = branchService.createBranch(request);
        Map<String, Object> legacyResponse = new HashMap<>();
        legacyResponse.put("success", true);
        legacyResponse.put("message", "Branch added successfully");
        legacyResponse.put("branchId", response.getId());
        legacyResponse.put("data", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(legacyResponse);
    }

    /**
     * Preserved legacy & modern endpoint: GET /api/v1/branches
     */
    @GetMapping("/api/v1/branches")
    public ResponseEntity<Map<String, Object>> getAllBranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "active") String status) {

        PageResponse<BranchResponse> pageResponse = branchService.getAllBranches(page, size, search, companyId, status);

        Map<String, Object> legacyResponse = new HashMap<>();
        legacyResponse.put("success", true);
        legacyResponse.put("message", "Branch List Fetched Successfully");
        legacyResponse.put("data", pageResponse.getContent());
        legacyResponse.put("currentPage", pageResponse.getPageNumber());
        legacyResponse.put("totalItems", pageResponse.getTotalElements());
        legacyResponse.put("totalPages", pageResponse.getTotalPages());
        return ResponseEntity.ok(legacyResponse);
    }

    /**
     * Modern RESTful endpoint: POST /api/v1/branches
     */
    @PostMapping("/api/v1/branches")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchCreateRequest request) {
        BranchResponse response = branchService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Branch created successfully", response));
    }

    @GetMapping("/api/v1/branches/by-company/{companyId}")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranchesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.success("Branches fetched", branchService.getActiveBranchesByCompany(companyId)));
    }

    @GetMapping("/api/v1/branches/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(@PathVariable Long id) {
        BranchResponse response = branchService.getBranchById(id);
        return ResponseEntity.ok(ApiResponse.success("Branch details retrieved", response));
    }

    @PutMapping("/api/v1/branches/{id}")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchUpdateRequest request) {
        BranchResponse response = branchService.updateBranch(id, request);
        return ResponseEntity.ok(ApiResponse.success("Branch updated successfully", response));
    }

    @PutMapping("/api/v1/branches/{id}/soft-delete")
    public ResponseEntity<ApiResponse<Void>> softDeleteBranch(@PathVariable Long id) {
        branchService.toggleBranchStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success("Branch deactivated successfully"));
    }

    @PutMapping("/api/v1/branches/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreBranch(@PathVariable Long id) {
        branchService.toggleBranchStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success("Branch activated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> legacyDeleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success("Branch deleted successfully"));
    }

    @DeleteMapping("/api/v1/branches/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success("Branch deleted successfully"));
    }
}
