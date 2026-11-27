package com.talentcore.controller.mvc;

import com.talentcore.dto.request.CompanyRequest;
import com.talentcore.dto.response.CompanyResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyMvcController {

    private final CompanyService companyService;

    @GetMapping
    public String listCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "active") String status,
            Model model) {

        PageResponse<CompanyResponse> companies = companyService.getAllCompanies(page, size, search, status);
        model.addAttribute("companies", companies);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("status", status);
        model.addAttribute("activePage", "companies");

        return "company/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("companyForm")) {
            model.addAttribute("companyForm", new CompanyRequest());
        }
        model.addAttribute("activePage", "companies");
        return "company/create";
    }

    @PostMapping("/create")
    public String createCompany(
            @Valid @ModelAttribute("companyForm") CompanyRequest companyForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "companies");
            return "company/create";
        }

        try {
            companyService.createCompany(companyForm);
            redirectAttributes.addFlashAttribute("successMessage", "Company successfully registered!");
            return "redirect:/companies";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("activePage", "companies");
            return "company/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        CompanyResponse comp = companyService.getCompanyById(id);
        CompanyRequest form = CompanyRequest.builder()
                .code(comp.getCode())
                .name(comp.getName())
                .shortName(comp.getShortName())
                .contactPersonName(comp.getContactPersonName())
                .contactPersonEmail(comp.getContactPersonEmail())
                .contactPersonPhone(comp.getContactPersonPhone())
                .address(comp.getAddress())
                .gstNo(comp.getGstNo())
                .panNo(comp.getPanNo())
                .cinNo(comp.getCinNo())
                .status(comp.getStatus())
                .build();

        model.addAttribute("companyForm", form);
        model.addAttribute("companyId", id);
        model.addAttribute("activePage", "companies");
        return "company/edit";
    }

    @GetMapping("/view/{id}")
    public String viewCompany(@PathVariable Long id, Model model) {
        CompanyResponse comp = companyService.getCompanyById(id);
        model.addAttribute("company", comp);
        model.addAttribute("activePage", "companies");
        return "company/view";
    }

    @PostMapping("/edit/{id}")
    public String updateCompany(
            @PathVariable Long id,
            @Valid @ModelAttribute("companyForm") CompanyRequest companyForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("companyId", id);
            model.addAttribute("activePage", "companies");
            return "company/edit";
        }

        try {
            companyService.updateCompany(id, companyForm);
            redirectAttributes.addFlashAttribute("successMessage", "Company updated successfully!");
            return "redirect:/companies";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("companyId", id);
            model.addAttribute("activePage", "companies");
            return "company/edit";
        }
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes redirectAttributes) {
        companyService.toggleCompanyStatus(id, active);
        redirectAttributes.addFlashAttribute("successMessage",
                active ? "Company activated successfully!" : "Company deactivated successfully!");
        return "redirect:/companies";
    }
}
