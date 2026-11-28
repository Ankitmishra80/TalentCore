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
public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private Long companyId;
    private String companyName;
    private Long branchId;
    private String branchName;
    private Long departmentId;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private LocalDate exitDate;
    private String mobileNumber;
    private String officialEmail;
    private String employmentType;
    private Long reportingManagerId;
    private String reportingManagerName;
    private String panNumber;
    private String aadharNumber;
    private String profilePhoto;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
