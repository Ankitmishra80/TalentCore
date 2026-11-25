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
public class DepartmentResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String departmentCode;
    private String departmentName;
    private String shortName;
    private String description;
    private Boolean status;
    private int designationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
