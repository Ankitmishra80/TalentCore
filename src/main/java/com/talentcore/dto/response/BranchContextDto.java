package com.talentcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchContextDto {
    private Long companyId;
    private String companyName;
    private Long activeBranchId;
    private String activeBranchName;
    private String activeBranchCode;
    private List<BranchResponse> accessibleBranches;
}
