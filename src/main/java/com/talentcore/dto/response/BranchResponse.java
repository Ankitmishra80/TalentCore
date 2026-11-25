package com.talentcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String branchCode;
    private String branchName;
    private String city;
    private String state;
    private String address;
    private String contactNumber;
    private String email;
    private Boolean status;
    private long activeEmployeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
