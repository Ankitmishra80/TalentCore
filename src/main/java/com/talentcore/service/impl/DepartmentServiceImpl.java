package com.talentcore.service.impl;

import com.talentcore.dto.request.DepartmentRequest;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.entity.Department;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.repository.DepartmentRepository;
import com.talentcore.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        if (departmentRepository.existsByCompanyIdAndDepartmentNameIgnoreCase(request.getCompanyId(), request.getDepartmentName())) {
            throw new BadRequestException("Department with name '" + request.getDepartmentName() + "' already exists for this company.");
        }

        Department department = Department.builder()
                .company(company)
                .departmentCode(request.getDepartmentCode())
                .departmentName(request.getDepartmentName())
                .shortName(request.getShortName())
                .description(request.getDescription())
                .isActive(request.getStatus() != null ? request.getStatus() : true)
                .build();

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        if (!dept.getDepartmentName().equalsIgnoreCase(request.getDepartmentName()) &&
            departmentRepository.existsByCompanyIdAndDepartmentNameIgnoreCase(request.getCompanyId(), request.getDepartmentName())) {
            throw new BadRequestException("Department with name '" + request.getDepartmentName() + "' already exists for this company.");
        }

        dept.setCompany(company);
        dept.setDepartmentCode(request.getDepartmentCode());
        dept.setDepartmentName(request.getDepartmentName());
        dept.setShortName(request.getShortName());
        dept.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            dept.setIsActive(request.getStatus());
        }

        Department updated = departmentRepository.save(dept);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department dept = departmentRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        return mapToResponse(dept);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getAllDepartments(int page, int size, String search, Long companyId, String status) {
        Boolean isActive = null;
        if ("active".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("inactive".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Department> deptPage = departmentRepository.findAllWithFilter(search, companyId, isActive, pageable);
        return PageResponse.from(deptPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getActiveDepartmentsByCompany(Long companyId) {
        return departmentRepository.findByCompanyIdAndIsActiveTrue(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleDepartmentStatus(Long id, boolean active) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        dept.setIsActive(active);
        departmentRepository.save(dept);
        return true;
    }

    private DepartmentResponse mapToResponse(Department dept) {
        return DepartmentResponse.builder()
                .id(dept.getId())
                .companyId(dept.getCompany() != null ? dept.getCompany().getId() : null)
                .companyName(dept.getCompany() != null ? dept.getCompany().getName() : null)
                .departmentCode(dept.getDepartmentCode())
                .departmentName(dept.getDepartmentName())
                .shortName(dept.getShortName())
                .description(dept.getDescription())
                .status(dept.getIsActive())
                .designationCount(dept.getDesignations() != null ? dept.getDesignations().size() : 0)
                .createdAt(dept.getCreatedAt())
                .updatedAt(dept.getUpdatedAt())
                .build();
    }
}
