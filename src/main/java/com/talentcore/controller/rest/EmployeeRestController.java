package com.talentcore.controller.rest;

import com.talentcore.dto.request.BranchAssignmentRequest;
import com.talentcore.dto.request.EmployeeRequest;
import com.talentcore.dto.response.ApiResponse;
import com.talentcore.dto.response.BranchEmployeeResponse;
import com.talentcore.dto.response.EmployeeResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.service.BranchEmployeeService;
import com.talentcore.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final BranchEmployeeService branchEmployeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long designationId,
            @RequestParam(defaultValue = "all") String status) {
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved",
                employeeService.getAllEmployees(page, size, search, companyId, departmentId, designationId, status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee profile retrieved", employeeService.getEmployeeById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Employee registered successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        employeeService.toggleEmployeeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Employee status updated to " + status));
    }

    // Branch assignment APIs
    @PostMapping("/assign-branch")
    public ResponseEntity<ApiResponse<BranchEmployeeResponse>> assignBranch(@Valid @RequestBody BranchAssignmentRequest request) {
        BranchEmployeeResponse response = branchEmployeeService.assignEmployeeToBranch(request);
        return ResponseEntity.ok(ApiResponse.success("Employee assigned to branch successfully", response));
    }

    @GetMapping("/{id}/branch-history")
    public ResponseEntity<ApiResponse<List<BranchEmployeeResponse>>> getBranchHistory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Branch assignment history retrieved",
                branchEmployeeService.getAssignmentHistoryByEmployee(id)));
    }
}
