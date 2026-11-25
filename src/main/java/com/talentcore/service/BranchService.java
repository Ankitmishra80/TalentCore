package com.talentcore.service;

import com.talentcore.dto.request.BranchCreateRequest;
import com.talentcore.dto.request.BranchUpdateRequest;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.dto.response.PageResponse;

import java.util.List;

public interface BranchService {

    BranchResponse createBranch(BranchCreateRequest request);

    BranchResponse updateBranch(Long id, BranchUpdateRequest request);

    BranchResponse getBranchById(Long id);

    PageResponse<BranchResponse> getAllBranches(int page, int size, String search, Long companyId, String status);

    List<BranchResponse> getActiveBranchesByCompany(Long companyId);

    List<BranchResponse> getAllActiveBranches();

    boolean toggleBranchStatus(Long id, boolean active);

    boolean deleteBranch(Long id);
}
