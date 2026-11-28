package com.talentcore.service;

import com.talentcore.dto.request.EmployeeRequest;
import com.talentcore.dto.response.EmployeeResponse;
import com.talentcore.dto.response.PageResponse;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByCode(String code);
    PageResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, Long companyId, Long departmentId, Long designationId, String status);
    List<EmployeeResponse> getActiveEmployeesByCompany(Long companyId);
    boolean toggleEmployeeStatus(Long id, String status);
    String generateUniqueEmployeeCode(Long companyId);
}
