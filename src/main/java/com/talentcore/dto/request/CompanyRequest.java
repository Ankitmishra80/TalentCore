package com.talentcore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company code is required")
    @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
    private String code;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    private String name;

    private String shortName;
    private String contactPersonName;

    @Email(message = "Valid email address required")
    private String contactPersonEmail;

    private String contactPersonPhone;
    private String address;
    private String gstNo;
    private String panNo;
    private String cinNo;

    @Builder.Default
    private Boolean status = true;
}
