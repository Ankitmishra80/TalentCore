package com.talentcore.controller.mvc;

import com.talentcore.dto.request.DesignationRequest;
import com.talentcore.dto.response.DepartmentResponse;
import com.talentcore.dto.response.DesignationResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.service.DepartmentService;
import com.talentcore.service.DesignationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/designations")
@RequiredArgsConstructor
public class DesignationMvcController {

    private final DesignationService designationService;
    private final DepartmentService departmentService;
    private final CompanyRepository companyRepository;

    @ModelAttribute("companies")
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @GetMapping
    public String listDesignations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "active") String status,
            Model model) {

        PageResponse<DesignationResponse> designations = designationService.getAllDesignations(page, size, search, companyId, departmentId, status);
        model.addAttribute("designations", designations);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("companyId", companyId);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("status", status);
        model.addAttribute("activePage", "designations");

        return "designation/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("desigForm")) {
            model.addAttribute("desigForm", new DesignationRequest());
        }
        List<Company> companies = companyRepository.findAll();
        Long firstCompId = companies.isEmpty() ? null : companies.get(0).getId();
        List<DepartmentResponse> depts = firstCompId != null ? departmentService.getActiveDepartmentsByCompany(firstCompId) : List.of();

        model.addAttribute("departments", depts);
        model.addAttribute("activePage", "designations");
        return "designation/create";
    }

    @PostMapping("/create")
    public String createDesignation(
            @Valid @ModelAttribute("desigForm") DesignationRequest desigForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getActiveDepartmentsByCompany(1L));
            model.addAttribute("activePage", "designations");
            return "designation/create";
        }

        try {
            designationService.createDesignation(desigForm);
            redirectAttributes.addFlashAttribute("successMessage", "Designation created successfully!");
            return "redirect:/designations";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getActiveDepartmentsByCompany(1L));
            model.addAttribute("activePage", "designations");
            return "designation/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DesignationResponse desig = designationService.getDesignationById(id);
        DesignationRequest form = DesignationRequest.builder()
                .departmentId(desig.getDepartmentId())
                .companyId(desig.getCompanyId())
                .name(desig.getName())
                .shortName(desig.getShortName())
                .description(desig.getDescription())
                .status(desig.getStatus())
                .build();

        model.addAttribute("desigForm", form);
        model.addAttribute("desigId", id);
        model.addAttribute("departments", departmentService.getActiveDepartmentsByCompany(desig.getCompanyId() != null ? desig.getCompanyId() : 1L));
        model.addAttribute("activePage", "designations");
        return "designation/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateDesignation(
            @PathVariable Long id,
            @Valid @ModelAttribute("desigForm") DesignationRequest desigForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("desigId", id);
            model.addAttribute("activePage", "designations");
            return "designation/edit";
        }

        try {
            designationService.updateDesignation(id, desigForm);
            redirectAttributes.addFlashAttribute("successMessage", "Designation updated successfully!");
            return "redirect:/designations";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("desigId", id);
            model.addAttribute("activePage", "designations");
            return "designation/edit";
        }
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes redirectAttributes) {
        designationService.toggleDesignationStatus(id, active);
        redirectAttributes.addFlashAttribute("successMessage",
                active ? "Designation activated successfully!" : "Designation deactivated successfully!");
        return "redirect:/designations";
    }
}
