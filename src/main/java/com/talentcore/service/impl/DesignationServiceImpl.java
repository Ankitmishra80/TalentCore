package com.talentcore.service.impl;

import com.talentcore.dto.request.DesignationRequest;
import com.talentcore.dto.response.DesignationResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.entity.Department;
import com.talentcore.entity.Designation;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.repository.DepartmentRepository;
import com.talentcore.repository.DesignationRepository;
import com.talentcore.service.DesignationService;
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
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DesignationResponse createDesignation(DesignationRequest request) {
        Department department = departmentRepository.findByIdWithCompany(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));

        if (designationRepository.existsByDepartmentIdAndNameIgnoreCase(request.getDepartmentId(), request.getName())) {
            throw new BadRequestException("Designation '" + request.getName() + "' already exists in department " + department.getDepartmentName());
        }

        Company company = department.getCompany();

        Designation designation = Designation.builder()
                .department(department)
                .company(company)
                .name(request.getName())
                .shortName(request.getShortName())
                .description(request.getDescription())
                .isActive(request.getStatus() != null ? request.getStatus() : true)
                .build();

        Designation saved = designationRepository.save(designation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DesignationResponse updateDesignation(Long id, DesignationRequest request) {
        Designation designation = designationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + id));

        Department department = departmentRepository.findByIdWithCompany(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));

        if (!designation.getName().equalsIgnoreCase(request.getName()) &&
            designationRepository.existsByDepartmentIdAndNameIgnoreCase(request.getDepartmentId(), request.getName())) {
            throw new BadRequestException("Designation '" + request.getName() + "' already exists in department " + department.getDepartmentName());
        }

        designation.setDepartment(department);
        designation.setCompany(department.getCompany());
        designation.setName(request.getName());
        designation.setShortName(request.getShortName());
        designation.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            designation.setIsActive(request.getStatus());
        }

        Designation updated = designationRepository.save(designation);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignationResponse getDesignationById(Long id) {
        Designation designation = designationRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + id));
        return mapToResponse(designation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DesignationResponse> getAllDesignations(int page, int size, String search, Long companyId, Long departmentId, String status) {
        Boolean isActive = null;
        if ("active".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("inactive".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Designation> desigPage = designationRepository.findAllWithFilter(search, companyId, departmentId, isActive, pageable);
        return PageResponse.from(desigPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignationResponse> getActiveDesignationsByDepartment(Long departmentId) {
        return designationRepository.findByDepartmentIdAndIsActiveTrue(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleDesignationStatus(Long id, boolean active) {
        Designation desig = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + id));
        desig.setIsActive(active);
        designationRepository.save(desig);
        return true;
    }

    private DesignationResponse mapToResponse(Designation desig) {
        Department dept = desig.getDepartment();
        Company comp = desig.getCompany() != null ? desig.getCompany() : (dept != null ? dept.getCompany() : null);
        return DesignationResponse.builder()
                .id(desig.getId())
                .companyId(comp != null ? comp.getId() : null)
                .companyName(comp != null ? comp.getName() : null)
                .departmentId(dept != null ? dept.getId() : null)
                .departmentName(dept != null ? dept.getDepartmentName() : null)
                .name(desig.getName())
                .shortName(desig.getShortName())
                .description(desig.getDescription())
                .status(desig.getIsActive())
                .createdAt(desig.getCreatedAt())
                .updatedAt(desig.getUpdatedAt())
                .build();
    }
}
