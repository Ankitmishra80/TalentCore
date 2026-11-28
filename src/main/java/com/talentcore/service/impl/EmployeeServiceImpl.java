package com.talentcore.service.impl;

import com.talentcore.dto.request.BranchAssignmentRequest;
import com.talentcore.dto.request.EmployeeRequest;
import com.talentcore.dto.response.EmployeeResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.*;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.repository.*;
import com.talentcore.service.BranchEmployeeService;
import com.talentcore.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final BranchEmployeeRepository branchEmployeeRepository;
    private final BranchEmployeeService branchEmployeeService;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + request.getDesignationId()));

        String code = request.getEmployeeCode();
        if (code == null || code.isBlank()) {
            code = generateUniqueEmployeeCode(company.getId());
        } else if (employeeRepository.existsByEmployeeCode(code)) {
            throw new BadRequestException("Employee code " + code + " already exists.");
        }

        Employee reportingManager = null;
        if (request.getReportingManagerId() != null) {
            reportingManager = employeeRepository.findById(request.getReportingManagerId()).orElse(null);
        }

        Employee employee = Employee.builder()
                .company(company)
                .department(department)
                .designation(designation)
                .employeeCode(code)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .joiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now())
                .exitDate(request.getExitDate())
                .mobileNumber(request.getMobileNumber())
                .officialEmail(request.getOfficialEmail())
                .panNumber(request.getPanNumber())
                .aadharNumber(request.getAadharNumber())
                .employmentType(request.getEmploymentType() != null ? request.getEmploymentType() : "FULL_TIME")
                .reportingManager(reportingManager)
                .address(request.getAddress())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        Employee saved = employeeRepository.save(employee);

        // Optional initial branch assignment
        if (request.getBranchId() != null) {
            branchEmployeeService.assignEmployeeToBranch(BranchAssignmentRequest.builder()
                    .employeeId(saved.getId())
                    .branchId(request.getBranchId())
                    .startDate(saved.getJoiningDate())
                    .build());
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee emp = employeeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + request.getDesignationId()));

        if (request.getEmployeeCode() != null && !request.getEmployeeCode().equalsIgnoreCase(emp.getEmployeeCode()) &&
            employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new BadRequestException("Employee code " + request.getEmployeeCode() + " is already taken.");
        }

        Employee reportingManager = null;
        if (request.getReportingManagerId() != null && !request.getReportingManagerId().equals(id)) {
            reportingManager = employeeRepository.findById(request.getReportingManagerId()).orElse(null);
        }

        emp.setCompany(company);
        emp.setDepartment(department);
        emp.setDesignation(designation);
        if (request.getEmployeeCode() != null && !request.getEmployeeCode().isBlank()) {
            emp.setEmployeeCode(request.getEmployeeCode());
        }
        emp.setFirstName(request.getFirstName());
        emp.setLastName(request.getLastName());
        emp.setGender(request.getGender());
        emp.setDateOfBirth(request.getDateOfBirth());
        emp.setJoiningDate(request.getJoiningDate());
        emp.setExitDate(request.getExitDate());
        emp.setMobileNumber(request.getMobileNumber());
        emp.setOfficialEmail(request.getOfficialEmail());
        emp.setPanNumber(request.getPanNumber());
        emp.setAadharNumber(request.getAadharNumber());
        emp.setEmploymentType(request.getEmploymentType());
        emp.setReportingManager(reportingManager);
        emp.setAddress(request.getAddress());
        if (request.getStatus() != null) {
            emp.setStatus(request.getStatus());
        }

        Employee updated = employeeRepository.save(emp);

        // If branch changed in request
        if (request.getBranchId() != null) {
            Optional<BranchEmployee> activeBranchOpt = branchEmployeeRepository.findByEmployeeIdAndIsActiveTrue(updated.getId());
            if (activeBranchOpt.isEmpty() || !activeBranchOpt.get().getBranch().getId().equals(request.getBranchId())) {
                branchEmployeeService.assignEmployeeToBranch(BranchAssignmentRequest.builder()
                        .employeeId(updated.getId())
                        .branchId(request.getBranchId())
                        .startDate(LocalDate.now())
                        .build());
            }
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee emp = employeeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return mapToResponse(emp);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByCode(String code) {
        Employee emp = employeeRepository.findByEmployeeCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + code));
        return mapToResponse(emp);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, Long companyId, Long departmentId, Long designationId, String status) {
        String statusFilter = (status == null || "all".equalsIgnoreCase(status)) ? null : status.toUpperCase();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Employee> empPage = employeeRepository.findAllWithFilter(search, companyId, departmentId, designationId, statusFilter, pageable);
        return PageResponse.from(empPage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getActiveEmployeesByCompany(Long companyId) {
        return employeeRepository.findByCompanyIdAndStatus(companyId, "ACTIVE").stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleEmployeeStatus(Long id, String status) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        emp.setStatus(status.toUpperCase());
        if ("INACTIVE".equalsIgnoreCase(status) || "TERMINATED".equalsIgnoreCase(status) || "RESIGNED".equalsIgnoreCase(status)) {
            if (emp.getExitDate() == null) {
                emp.setExitDate(LocalDate.now());
            }
        }
        employeeRepository.save(emp);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public String generateUniqueEmployeeCode(Long companyId) {
        long count = employeeRepository.countByCompanyId(companyId);
        String code;
        int increment = 1;
        do {
            code = String.format("TC-EMP-%04d", count + increment);
            increment++;
        } while (employeeRepository.existsByEmployeeCode(code));
        return code;
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        Optional<BranchEmployee> activeBranchOpt = branchEmployeeRepository.findByEmployeeIdAndIsActiveTrue(emp.getId());
        Long branchId = activeBranchOpt.map(be -> be.getBranch().getId()).orElse(null);
        String branchName = activeBranchOpt.map(be -> be.getBranch().getBranchName()).orElse("Unassigned");

        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .fullName(emp.getFullName())
                .companyId(emp.getCompany() != null ? emp.getCompany().getId() : null)
                .companyName(emp.getCompany() != null ? emp.getCompany().getName() : null)
                .branchId(branchId)
                .branchName(branchName)
                .departmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : null)
                .designationId(emp.getDesignation() != null ? emp.getDesignation().getId() : null)
                .designationName(emp.getDesignation() != null ? emp.getDesignation().getName() : null)
                .gender(emp.getGender())
                .dateOfBirth(emp.getDateOfBirth())
                .joiningDate(emp.getJoiningDate())
                .exitDate(emp.getExitDate())
                .mobileNumber(emp.getMobileNumber())
                .officialEmail(emp.getOfficialEmail())
                .employmentType(emp.getEmploymentType())
                .reportingManagerId(emp.getReportingManager() != null ? emp.getReportingManager().getId() : null)
                .reportingManagerName(emp.getReportingManager() != null ? emp.getReportingManager().getFullName() : null)
                .panNumber(emp.getPanNumber())
                .aadharNumber(emp.getAadharNumber())
                .profilePhoto(emp.getProfilePhoto())
                .address(emp.getAddress())
                .status(emp.getStatus())
                .createdAt(emp.getCreatedAt())
                .updatedAt(emp.getUpdatedAt())
                .build();
    }
}
