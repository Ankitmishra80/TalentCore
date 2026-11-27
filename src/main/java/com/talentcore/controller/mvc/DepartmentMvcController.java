package com.talentcore.controller.mvc;

import com.talentcore.dto.request.DepartmentRequest;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentMvcController {

    private final DepartmentService departmentService;
    private final CompanyRepository companyRepository;

    @ModelAttribute("companies")
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @GetMapping
    public String listDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "active") String status,
            Model model) {

        PageResponse<DepartmentResponse> departments = departmentService.getAllDepartments(page, size, search, companyId, status);
        model.addAttribute("departments", departments);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("companyId", companyId);
        model.addAttribute("status", status);
        model.addAttribute("activePage", "departments");

        return "department/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("deptForm")) {
            model.addAttribute("deptForm", new DepartmentRequest());
        }
        model.addAttribute("activePage", "departments");
        return "department/create";
    }

    @PostMapping("/create")
    public String createDepartment(
            @Valid @ModelAttribute("deptForm") DepartmentRequest deptForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "departments");
            return "department/create";
        }

        try {
            departmentService.createDepartment(deptForm);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
            return "redirect:/departments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("activePage", "departments");
            return "department/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DepartmentResponse dept = departmentService.getDepartmentById(id);
        DepartmentRequest form = DepartmentRequest.builder()
                .companyId(dept.getCompanyId())
                .departmentCode(dept.getDepartmentCode())
                .departmentName(dept.getDepartmentName())
                .shortName(dept.getShortName())
                .description(dept.getDescription())
                .status(dept.getStatus())
                .build();

        model.addAttribute("deptForm", form);
        model.addAttribute("deptId", id);
        model.addAttribute("activePage", "departments");
        return "department/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateDepartment(
            @PathVariable Long id,
            @Valid @ModelAttribute("deptForm") DepartmentRequest deptForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("deptId", id);
            model.addAttribute("activePage", "departments");
            return "department/edit";
        }

        try {
            departmentService.updateDepartment(id, deptForm);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
            return "redirect:/departments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("deptId", id);
            model.addAttribute("activePage", "departments");
            return "department/edit";
        }
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes redirectAttributes) {
        departmentService.toggleDepartmentStatus(id, active);
        redirectAttributes.addFlashAttribute("successMessage",
                active ? "Department activated successfully!" : "Department deactivated successfully!");
        return "redirect:/departments";
    }
}
