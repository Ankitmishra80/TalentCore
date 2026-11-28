package com.talentcore.service.impl;

import com.talentcore.dto.request.BranchAssignmentRequest;
import com.talentcore.dto.response.BranchEmployeeResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Branch;
import com.talentcore.entity.BranchEmployee;
import com.talentcore.entity.Employee;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.repository.BranchEmployeeRepository;
import com.talentcore.repository.BranchRepository;
import com.talentcore.repository.EmployeeRepository;
import com.talentcore.service.BranchEmployeeService;
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
public class BranchEmployeeServiceImpl implements BranchEmployeeService {

    private final BranchEmployeeRepository branchEmployeeRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public BranchEmployeeResponse assignEmployeeToBranch(BranchAssignmentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + request.getBranchId()));

        if (!branch.getIsActive()) {
            throw new BadRequestException("Cannot assign employee to an inactive branch.");
        }

        // Close any existing active assignment for this employee
        Optional<BranchEmployee> activeOpt = branchEmployeeRepository.findByEmployeeIdAndIsActiveTrue(employee.getId());
        if (activeOpt.isPresent()) {
            BranchEmployee existing = activeOpt.get();
            if (existing.getBranch().getId().equals(branch.getId())) {
                throw new BadRequestException("Employee is already actively assigned to branch " + branch.getBranchName());
            }
            existing.setIsActive(false);
            existing.setEndDate(request.getStartDate().minusDays(1));
            branchEmployeeRepository.save(existing);
        }

        BranchEmployee assignment = BranchEmployee.builder()
                .employee(employee)
                .branch(branch)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();

        BranchEmployee saved = branchEmployeeRepository.save(assignment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public BranchEmployeeResponse transferEmployee(Long employeeId, Long newBranchId, String effectiveDateStr) {
        LocalDate effectiveDate = (effectiveDateStr != null && !effectiveDateStr.isBlank()) 
                ? LocalDate.parse(effectiveDateStr) : LocalDate.now();

        BranchAssignmentRequest request = BranchAssignmentRequest.builder()
                .employeeId(employeeId)
                .branchId(newBranchId)
                .startDate(effectiveDate)
                .build();

        return assignEmployeeToBranch(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BranchEmployeeResponse> getEmployeesByBranch(Long branchId, int page, int size, String search, String status) {
        Boolean isActive = null;
        if ("active".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("inactive".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate"));
        Page<BranchEmployee> bePage = branchEmployeeRepository.findByBranchWithFilter(branchId, search, isActive, pageable);
        return PageResponse.from(bePage.map(this::mapToResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchEmployeeResponse> getAssignmentHistoryByEmployee(Long employeeId) {
        return branchEmployeeRepository.findHistoryByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleAssignmentStatus(Long assignmentId, boolean active) {
        BranchEmployee be = branchEmployeeRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch assignment not found with ID: " + assignmentId));
        be.setIsActive(active);
        branchEmployeeRepository.save(be);
        return true;
    }

    private BranchEmployeeResponse mapToResponse(BranchEmployee be) {
        Employee emp = be.getEmployee();
        Branch b = be.getBranch();
        return BranchEmployeeResponse.builder()
                .id(be.getId())
                .branchId(b.getId())
                .branchName(b.getBranchName())
                .branchCode(b.getBranchCode())
                .employeeId(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .employeeName(emp.getFullName())
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : null)
                .designationName(emp.getDesignation() != null ? emp.getDesignation().getName() : null)
                .startDate(be.getStartDate())
                .endDate(be.getEndDate())
                .status(be.getIsActive())
                .createdAt(be.getCreatedAt())
                .build();
    }
}
