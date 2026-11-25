package com.talentcore.service;

import com.talentcore.dto.request.DepartmentRequest;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.PageResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
    DepartmentResponse getDepartmentById(Long id);
    PageResponse<DepartmentResponse> getAllDepartments(int page, int size, String search, Long companyId, String status);
    List<DepartmentResponse> getActiveDepartmentsByCompany(Long companyId);
    boolean toggleDepartmentStatus(Long id, boolean active);
}
