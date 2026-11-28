package com.talentcore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEmployeeResponse {
    private Long id;
    private Long branchId;
    private String branchName;
    private String branchCode;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String departmentName;
    private String designationName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean status;
    private LocalDateTime createdAt;
}
