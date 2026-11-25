package com.talentcore.service;

import com.talentcore.dto.request.DesignationRequest;
import com.talentcore.dto.response.DesignationResponse;
import com.talentcore.dto.response.PageResponse;

import java.util.List;

public interface DesignationService {
    DesignationResponse createDesignation(DesignationRequest request);
    DesignationResponse updateDesignation(Long id, DesignationRequest request);
    DesignationResponse getDesignationById(Long id);
    PageResponse<DesignationResponse> getAllDesignations(int page, int size, String search, Long companyId, Long departmentId, String status);
    List<DesignationResponse> getActiveDesignationsByDepartment(Long departmentId);
    boolean toggleDesignationStatus(Long id, boolean active);
}
