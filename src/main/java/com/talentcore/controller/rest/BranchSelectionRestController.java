package com.talentcore.controller.rest;

import com.talentcore.dto.response.ApiResponse;
import com.talentcore.dto.response.BranchContextDto;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.security.CustomUserDetails;
import com.talentcore.service.BranchSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branch-selection")
@RequiredArgsConstructor
public class BranchSelectionRestController {

    private final BranchSelectionService branchSelectionService;

    @GetMapping("/accessible")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAccessibleBranches(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BranchResponse> branches = branchSelectionService.getAccessibleBranches(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Accessible branches retrieved", branches));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<BranchContextDto>> getCurrentContext(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BranchContextDto context = branchSelectionService.getCurrentContext(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Current active branch context retrieved", context));
    }

    @PostMapping("/select/{branchId}")
    public ResponseEntity<ApiResponse<BranchContextDto>> selectBranch(
            @PathVariable Long branchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        BranchContextDto updatedContext = branchSelectionService.selectActiveBranch(userDetails.getId(), branchId);
        return ResponseEntity.ok(ApiResponse.success("Active branch switched successfully", updatedContext));
    }
}
