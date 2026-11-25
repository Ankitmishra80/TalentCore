package com.talentcore.service.impl;

import com.talentcore.dto.request.BranchCreateRequest;
import com.talentcore.dto.request.BranchUpdateRequest;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Branch;
import com.talentcore.entity.Company;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.mapper.BranchMapper;
import com.talentcore.repository.BranchEmployeeRepository;
import com.talentcore.repository.BranchRepository;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.service.BranchService;
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
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final BranchEmployeeRepository branchEmployeeRepository;

    @Override
    @Transactional
    public BranchResponse createBranch(BranchCreateRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        if (branchRepository.existsByCompanyIdAndBranchCode(request.getCompanyId(), request.getBranchCode())) {
            throw new BadRequestException("Branch with code " + request.getBranchCode() + " already exists for this company");
        }

        Branch branch = BranchMapper.toEntity(request, company);
        Branch saved = branchRepository.save(branch);
        return BranchMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    public BranchResponse updateBranch(Long id, BranchUpdateRequest request) {
        Branch branch = branchRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        if (!branch.getBranchCode().equalsIgnoreCase(request.getBranchCode()) &&
            branchRepository.existsByCompanyIdAndBranchCode(request.getCompanyId(), request.getBranchCode())) {
            throw new BadRequestException("Branch code " + request.getBranchCode() + " is already in use for this company");
        }

        branch.setCompany(company);
        branch.setBranchCode(request.getBranchCode());
        branch.setBranchName(request.getBranchName());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setAddress(request.getAddress());
        branch.setContactNumber(request.getContactNumber());
        branch.setEmail(request.getEmail());
        if (request.getStatus() != null) {
            branch.setIsActive(request.getStatus());
        }

        Branch updated = branchRepository.save(branch);
        long activeCount = branchEmployeeRepository.countByBranchIdAndIsActiveTrue(updated.getId());
        return BranchMapper.toResponse(updated, activeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
        long activeCount = branchEmployeeRepository.countByBranchIdAndIsActiveTrue(branch.getId());
        return BranchMapper.toResponse(branch, activeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BranchResponse> getAllBranches(int page, int size, String search, Long companyId, String status) {
        Boolean isActive = null;
        if ("active".equalsIgnoreCase(status)) {
            isActive = true;
        } else if ("inactive".equalsIgnoreCase(status)) {
            isActive = false;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Branch> branchPage = branchRepository.findAllWithFilter(search, companyId, isActive, pageable);

        Page<BranchResponse> responsePage = branchPage.map(b -> {
            long activeCount = branchEmployeeRepository.countByBranchIdAndIsActiveTrue(b.getId());
            return BranchMapper.toResponse(b, activeCount);
        });

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getActiveBranchesByCompany(Long companyId) {
        return branchRepository.findByCompanyIdAndIsActiveTrue(companyId).stream()
                .map(b -> BranchMapper.toResponse(b, branchEmployeeRepository.countByBranchIdAndIsActiveTrue(b.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllActiveBranches() {
        return branchRepository.findByIsActiveTrue().stream()
                .map(b -> BranchMapper.toResponse(b, branchEmployeeRepository.countByBranchIdAndIsActiveTrue(b.getId())))
                .toList();
    }

    @Override
    @Transactional
    public boolean toggleBranchStatus(Long id, boolean active) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
        branch.setIsActive(active);
        branchRepository.save(branch);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        long employeeCount = branchEmployeeRepository.countByBranchIdAndIsActiveTrue(id);
        if (employeeCount > 0) {
            throw new BadRequestException("Cannot delete branch because it has " + employeeCount + " active employee assignment(s). Please transfer employees first or deactivate the branch.");
        }

        branchRepository.delete(branch);
        return true;
    }
}
