package com.talentcore.service.impl;

import com.talentcore.dto.request.CompanyRequest;
import com.talentcore.dto.response.CompanyResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Company code already exists: " + request.getCode());
        }

        Company company = Company.builder()
                .code(request.getCode())
                .name(request.getName())
                .shortName(request.getShortName())
                .contactPersonName(request.getContactPersonName())
                .contactPersonEmail(request.getContactPersonEmail())
                .contactPersonPhone(request.getContactPersonPhone())
                .address(request.getAddress())
                .gstNo(request.getGstNo())
                .panNo(request.getPanNo())
                .cinNo(request.getCinNo())
                .isActive(request.getStatus() != null ? request.getStatus() : true)
                .build();

        Company saved = companyRepository.save(company);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        if (!company.getCode().equalsIgnoreCase(request.getCode()) && companyRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Company code already in use: " + request.getCode());
        }

        company.setCode(request.getCode());
        company.setName(request.getName());
        company.setShortName(request.getShortName());
        company.setContactPersonName(request.getContactPersonName());
        company.setContactPersonEmail(request.getContactPersonEmail());
        company.setContactPersonPhone(request.getContactPersonPhone());
        company.setAddress(request.getAddress());
        company.setGstNo(request.getGstNo());
        company.setPanNo(request.getPanNo());
        company.setCinNo(request.getCinNo());
        if (request.getStatus() != null) {
            company.setIsActive(request.getStatus());
        }

        Company updated = companyRepository.save(company);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
        return mapToResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CompanyResponse> getAllCompanies(int page, int size, String search, String status) {
        Boolean isActive = null;
        if ("active".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("inactive".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Company> companyPage = companyRepository.findAllWithFilter(search, isActive, pageable);
        return PageResponse.from(companyPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getAllActiveCompanies() {
        return companyRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleCompanyStatus(Long id, boolean active) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
        company.setIsActive(active);
        companyRepository.save(company);
        return true;
    }

    private CompanyResponse mapToResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .code(company.getCode())
                .name(company.getName())
                .shortName(company.getShortName())
                .contactPersonName(company.getContactPersonName())
                .contactPersonEmail(company.getContactPersonEmail())
                .contactPersonPhone(company.getContactPersonPhone())
                .address(company.getAddress())
                .gstNo(company.getGstNo())
                .panNo(company.getPanNo())
                .cinNo(company.getCinNo())
                .status(company.getIsActive())
                .branchCount(company.getBranches() != null ? company.getBranches().size() : 0)
                .employeeCount(0)
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}
