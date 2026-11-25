package com.talentcore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchCreateRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Branch code is required")
    @Size(min = 2, max = 50, message = "Branch code must be between 2 and 50 characters")
    private String branchCode;

    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 150, message = "Branch name must be between 2 and 150 characters")
    private String branchName;

    private String city;
    private String state;
    private String address;
    private String contactNumber;

    @Email(message = "Valid email required")
    private String email;
    
    @Builder.Default
    private Boolean status = true;
}
