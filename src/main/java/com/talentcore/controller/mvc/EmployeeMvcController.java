package com.talentcore.controller.mvc;

import com.talentcore.dto.request.EmployeeRequest;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.DesignationResponse;
import com.talentcore.dto.response.EmployeeResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.service.BranchService;
import com.talentcore.service.DepartmentService;
import com.talentcore.service.DesignationService;
import com.talentcore.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeMvcController {

    private final EmployeeService employeeService;
    private final CompanyRepository companyRepository;
    private final BranchService branchService;
    private final DepartmentService departmentService;
    private final DesignationService designationService;

    @ModelAttribute("companies")
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @GetMapping
    public String listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long designationId,
            @RequestParam(defaultValue = "all") String status,
            Model model) {

        PageResponse<EmployeeResponse> employees = employeeService.getAllEmployees(page, size, search, companyId, departmentId, designationId, status);
        model.addAttribute("employees", employees);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("companyId", companyId);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("designationId", designationId);
        model.addAttribute("status", status);
        model.addAttribute("departments", departmentService.getAllDepartments(0, 100, null, companyId, "active").getContent());
        model.addAttribute("designations", designationService.getAllDesignations(0, 100, null, companyId, departmentId, "active").getContent());
        model.addAttribute("activePage", "employees");

        return "employee/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("empForm")) {
            model.addAttribute("empForm", new EmployeeRequest());
        }

        List<Company> companies = companyRepository.findAll();
        Long firstCompId = companies.isEmpty() ? null : companies.get(0).getId();

        List<BranchResponse> branches = firstCompId != null ? branchService.getActiveBranchesByCompany(firstCompId) : List.of();
        List<DepartmentResponse> departments = firstCompId != null ? departmentService.getActiveDepartmentsByCompany(firstCompId) : List.of();
        List<DesignationResponse> designations = (!departments.isEmpty()) ? designationService.getActiveDesignationsByDepartment(departments.get(0).getId()) : List.of();

        model.addAttribute("branches", branches);
        model.addAttribute("departments", departments);
        model.addAttribute("designations", designations);
        model.addAttribute("activePage", "employees");
        return "employee/create";
    }

    @PostMapping("/create")
    public String createEmployee(
            @Valid @ModelAttribute("empForm") EmployeeRequest empForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateFormLookups(model, empForm.getCompanyId(), empForm.getDepartmentId());
            model.addAttribute("activePage", "employees");
            return "employee/create";
        }

        try {
            employeeService.createEmployee(empForm);
            redirectAttributes.addFlashAttribute("successMessage", "Employee enrolled successfully!");
            return "redirect:/employees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            populateFormLookups(model, empForm.getCompanyId(), empForm.getDepartmentId());
            model.addAttribute("activePage", "employees");
            return "employee/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EmployeeResponse emp = employeeService.getEmployeeById(id);
        EmployeeRequest form = EmployeeRequest.builder()
                .employeeCode(emp.getEmployeeCode())
                .companyId(emp.getCompanyId())
                .branchId(emp.getBranchId())
                .departmentId(emp.getDepartmentId())
                .designationId(emp.getDesignationId())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .gender(emp.getGender())
                .dateOfBirth(emp.getDateOfBirth())
                .joiningDate(emp.getJoiningDate())
                .exitDate(emp.getExitDate())
                .mobileNumber(emp.getMobileNumber())
                .officialEmail(emp.getOfficialEmail())
                .employmentType(emp.getEmploymentType())
                .reportingManagerId(emp.getReportingManagerId())
                .panNumber(emp.getPanNumber())
                .aadharNumber(emp.getAadharNumber())
                .address(emp.getAddress())
                .status(emp.getStatus())
                .build();

        model.addAttribute("empForm", form);
        model.addAttribute("empId", id);
        populateFormLookups(model, emp.getCompanyId(), emp.getDepartmentId());
        model.addAttribute("activePage", "employees");
        return "employee/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("empForm") EmployeeRequest empForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("empId", id);
            populateFormLookups(model, empForm.getCompanyId(), empForm.getDepartmentId());
            model.addAttribute("activePage", "employees");
            return "employee/edit";
        }

        try {
            employeeService.updateEmployee(id, empForm);
            redirectAttributes.addFlashAttribute("successMessage", "Employee profile updated successfully!");
            return "redirect:/employees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("empId", id);
            populateFormLookups(model, empForm.getCompanyId(), empForm.getDepartmentId());
            model.addAttribute("activePage", "employees");
            return "employee/edit";
        }
    }

    @GetMapping("/view/{id}")
    public String viewProfile(@PathVariable Long id, Model model) {
        EmployeeResponse emp = employeeService.getEmployeeById(id);
        model.addAttribute("employee", emp);
        model.addAttribute("activePage", "employees");
        return "employee/view";
    }

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        employeeService.toggleEmployeeStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Employee status changed to " + status);
        return "redirect:/employees";
    }

    private void populateFormLookups(Model model, Long companyId, Long departmentId) {
        Long compId = companyId != null ? companyId : 1L;
        model.addAttribute("branches", branchService.getActiveBranchesByCompany(compId));
        model.addAttribute("departments", departmentService.getActiveDepartmentsByCompany(compId));
        if (departmentId != null) {
            model.addAttribute("designations", designationService.getActiveDesignationsByDepartment(departmentId));
        } else {
            List<DepartmentResponse> depts = departmentService.getActiveDepartmentsByCompany(compId);
            if (!depts.isEmpty()) {
                model.addAttribute("designations", designationService.getActiveDesignationsByDepartment(depts.get(0).getId()));
            } else {
                model.addAttribute("designations", List.of());
            }
        }
        model.addAttribute("managers", employeeService.getActiveEmployeesByCompany(compId));
    }
}
