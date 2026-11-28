package com.talentcore.service;

import com.talentcore.dto.response.BranchContextDto;
import com.talentcore.dto.response.BranchResponse;

import java.util.List;

public interface BranchSelectionService {
    List<BranchResponse> getAccessibleBranches(Long userId);
    BranchContextDto selectActiveBranch(Long userId, Long branchId);
    BranchContextDto getCurrentContext(Long userId);
    boolean validateBranchAccess(Long userId, Long branchId);
}
