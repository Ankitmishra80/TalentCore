package com.talentcore.dto.request;

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
public class DesignationRequest {

    @NotNull(message = "Department is required")
    private Long departmentId;

    private Long companyId;

    @NotBlank(message = "Designation name is required")
    @Size(min = 2, max = 100, message = "Designation name must be between 2 and 100 characters")
    private String name;

    private String shortName;
    private String description;

    @Builder.Default
    private Boolean status = true;
}
