package com.talentcore.controller.rest;

import com.talentcore.dto.request.CompanyRequest;
import com.talentcore.dto.response.ApiResponse;
import com.talentcore.dto.response.CompanyResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.service.CompanyService;
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
public class CompanyRestController {

    private final CompanyService companyService;

    /**
     * Preserved legacy endpoint: GET /api/v1/all-companies
     */
    @GetMapping("/api/v1/all-companies")
    public ResponseEntity<Map<String, Object>> legacyGetAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "active") String status) {

        PageResponse<CompanyResponse> pageResponse = companyService.getAllCompanies(page, size, search, status);
        Map<String, Object> legacyResponse = new HashMap<>();
        legacyResponse.put("responseCode", 200);
        legacyResponse.put("responseDescription", "Company List Fetched Successfully");
        legacyResponse.put("data", pageResponse);
        return ResponseEntity.ok(legacyResponse);
    }

    @GetMapping("/api/v1/companies/active")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getActiveCompanies() {
        return ResponseEntity.ok(ApiResponse.success("Active companies fetched", companyService.getAllActiveCompanies()));
    }

    @GetMapping("/api/v1/companies/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Company retrieved", companyService.getCompanyById(id)));
    }

    @PostMapping("/api/v1/companies")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse created = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Company created", created));
    }

    @PutMapping("/api/v1/companies/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        CompanyResponse updated = companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success("Company updated", updated));
    }

    @PatchMapping("/api/v1/companies/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        companyService.toggleCompanyStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(active ? "Company activated" : "Company deactivated"));
    }
}
