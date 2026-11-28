package com.talentcore.service;

import com.talentcore.dto.request.BranchAssignmentRequest;
import com.talentcore.dto.response.BranchEmployeeResponse;
import com.talentcore.dto.response.PageResponse;

import java.util.List;

public interface BranchEmployeeService {
    BranchEmployeeResponse assignEmployeeToBranch(BranchAssignmentRequest request);
    BranchEmployeeResponse transferEmployee(Long employeeId, Long newBranchId, String effectiveDate);
    PageResponse<BranchEmployeeResponse> getEmployeesByBranch(Long branchId, int page, int size, String search, String status);
    List<BranchEmployeeResponse> getAssignmentHistoryByEmployee(Long employeeId);
    boolean toggleAssignmentStatus(Long assignmentId, boolean active);
}
