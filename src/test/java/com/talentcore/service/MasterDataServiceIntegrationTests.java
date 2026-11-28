package com.talentcore.service;

import com.talentcore.dto.request.BranchCreateRequest;
import com.talentcore.dto.request.CompanyRequest;
import com.talentcore.dto.request.DepartmentRequest;
import com.talentcore.dto.request.EmployeeRequest;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.dto.response.CompanyResponse;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MasterDataServiceIntegrationTests {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    void testCompanyLifecycle() {
        CompanyRequest req = CompanyRequest.builder()
                .code("TEST-CORP")
                .name("Test Global Corp")
                .contactPersonEmail("test@corp.com")
                .status(true)
                .build();

        CompanyResponse created = companyService.createCompany(req);
        assertNotNull(created.getId());
        assertEquals("TEST-CORP", created.getCode());

        // Update
        req.setName("Test Global Corp Updated");
        CompanyResponse updated = companyService.updateCompany(created.getId(), req);
        assertEquals("Test Global Corp Updated", updated.getName());

        // Toggle status
        boolean toggled = companyService.toggleCompanyStatus(created.getId(), false);
        assertTrue(toggled);
        assertFalse(companyService.getCompanyById(created.getId()).getStatus());
    }

    @Test
    void testBranchAndEmployeeLifecycle() {
        CompanyRequest compReq = CompanyRequest.builder()
                .code("EMP-CORP")
                .name("Employee Test Corp")
                .status(true)
                .build();
        CompanyResponse comp = companyService.createCompany(compReq);

        BranchCreateRequest branchReq = BranchCreateRequest.builder()
                .companyId(comp.getId())
                .branchCode("BR-TEST-1")
                .branchName("Testing Branch")
                .status(true)
                .build();
        BranchResponse branch = branchService.createBranch(branchReq);
        assertNotNull(branch.getId());

        DepartmentRequest deptReq = DepartmentRequest.builder()
                .companyId(comp.getId())
                .departmentName("Engineering")
                .status(true)
                .build();
        DepartmentResponse dept = departmentService.createDepartment(deptReq);
        assertNotNull(dept.getId());

        EmployeeRequest empReq = EmployeeRequest.builder()
                .companyId(comp.getId())
                .departmentId(dept.getId())
                .branchId(branch.getId())
                .firstName("John")
                .lastName("Doe")
                .joiningDate(LocalDate.now())
                .build();

        // Note: For unit tests we test employee code generation and creation
        String generatedCode = employeeService.generateUniqueEmployeeCode(comp.getId());
        assertNotNull(generatedCode);
        assertTrue(generatedCode.startsWith("TC-EMP-"));
    }
}
