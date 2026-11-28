package com.talentcore.service.impl;

import com.talentcore.dto.response.BranchContextDto;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.entity.Branch;
import com.talentcore.entity.BranchSelection;
import com.talentcore.entity.User;
import com.talentcore.exception.BadRequestException;
import com.talentcore.exception.ResourceNotFoundException;
import com.talentcore.mapper.BranchMapper;
import com.talentcore.repository.BranchEmployeeRepository;
import com.talentcore.repository.BranchRepository;
import com.talentcore.repository.BranchSelectionRepository;
import com.talentcore.repository.UserRepository;
import com.talentcore.service.BranchSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BranchSelectionServiceImpl implements BranchSelectionService {

    private final BranchSelectionRepository branchSelectionRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final BranchEmployeeRepository branchEmployeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAccessibleBranches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        if (companyId == null) {
            return List.of();
        }

        return branchRepository.findByCompanyIdAndIsActiveTrue(companyId).stream()
                .map(b -> BranchMapper.toResponse(b, branchEmployeeRepository.countByBranchIdAndIsActiveTrue(b.getId())))
                .toList();
    }

    @Override
    @Transactional
    public BranchContextDto selectActiveBranch(Long userId, Long branchId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Branch branch = branchRepository.findByIdWithCompany(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));

        if (!validateBranchAccess(userId, branchId)) {
            throw new BadRequestException("Unauthorized branch selection: you do not have permission to access branch " + branch.getBranchName());
        }

        Optional<BranchSelection> activeOpt = branchSelectionRepository.findActiveSelectionByUser(userId);
        if (activeOpt.isPresent()) {
            BranchSelection current = activeOpt.get();
            current.setIsActive(false);
            branchSelectionRepository.save(current);
        }

        BranchSelection newSelection = BranchSelection.builder()
                .user(user)
                .branch(branch)
                .selectedAt(LocalDateTime.now())
                .isActive(true)
                .build();
        branchSelectionRepository.save(newSelection);

        return getCurrentContext(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchContextDto getCurrentContext(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<BranchResponse> accessibleBranches = getAccessibleBranches(userId);
        Optional<BranchSelection> activeOpt = branchSelectionRepository.findActiveSelectionByUser(userId);

        Branch activeBranch = activeOpt.map(BranchSelection::getBranch)
                .or(() -> accessibleBranches.isEmpty() ? Optional.empty() : 
                          branchRepository.findById(accessibleBranches.get(0).getId()))
                .orElse(null);

        return BranchContextDto.builder()
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getName() : "TalentCore")
                .activeBranchId(activeBranch != null ? activeBranch.getId() : null)
                .activeBranchName(activeBranch != null ? activeBranch.getBranchName() : "All Branches")
                .activeBranchCode(activeBranch != null ? activeBranch.getBranchCode() : "GLOBAL")
                .accessibleBranches(accessibleBranches)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateBranchAccess(Long userId, Long branchId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getCompany() == null) {
            return false;
        }

        Branch branch = branchRepository.findById(branchId).orElse(null);
        if (branch == null || !branch.getIsActive()) {
            return false;
        }

        // Verify branch belongs to user's company
        return user.getCompany().getId().equals(branch.getCompany().getId());
    }
}
