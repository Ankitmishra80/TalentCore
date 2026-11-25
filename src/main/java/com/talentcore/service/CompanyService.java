package com.talentcore.service;

import com.talentcore.dto.request.CompanyRequest;
import com.talentcore.dto.response.CompanyResponse;
import com.talentcore.dto.response.PageResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse updateCompany(Long id, CompanyRequest request);
    CompanyResponse getCompanyById(Long id);
    PageResponse<CompanyResponse> getAllCompanies(int page, int size, String search, String status);
    List<CompanyResponse> getAllActiveCompanies();
    boolean toggleCompanyStatus(Long id, boolean active);
}
