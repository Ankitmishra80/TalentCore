package com.talentcore.controller.mvc;

import com.talentcore.dto.request.BranchCreateRequest;
import com.talentcore.dto.request.BranchUpdateRequest;
import com.talentcore.dto.response.BranchResponse;
import com.talentcore.dto.response.PageResponse;
import com.talentcore.entity.Company;
import com.talentcore.repository.CompanyRepository;
import com.talentcore.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchMvcController {

    private final BranchService branchService;
    private final CompanyRepository companyRepository;

    @ModelAttribute("companies")
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @GetMapping
    public String listBranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "active") String status,
            Model model) {

        PageResponse<BranchResponse> branches = branchService.getAllBranches(page, size, search, companyId, status);
        model.addAttribute("branches", branches);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("companyId", companyId);
        model.addAttribute("status", status);
        model.addAttribute("activePage", "branches");

        return "branch/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("branchForm")) {
            model.addAttribute("branchForm", new BranchCreateRequest());
        }
        model.addAttribute("activePage", "branches");
        return "branch/create";
    }

    @PostMapping("/create")
    public String createBranch(
            @Valid @ModelAttribute("branchForm") BranchCreateRequest branchForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "branches");
            return "branch/create";
        }

        try {
            branchService.createBranch(branchForm);
            redirectAttributes.addFlashAttribute("successMessage", "Branch successfully created!");
            return "redirect:/branches";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("activePage", "branches");
            return "branch/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BranchResponse branch = branchService.getBranchById(id);
        BranchUpdateRequest updateRequest = BranchUpdateRequest.builder()
                .companyId(branch.getCompanyId())
                .branchCode(branch.getBranchCode())
                .branchName(branch.getBranchName())
                .city(branch.getCity())
                .state(branch.getState())
                .address(branch.getAddress())
                .contactNumber(branch.getContactNumber())
                .email(branch.getEmail())
                .status(branch.getStatus())
                .build();

        model.addAttribute("branchForm", updateRequest);
        model.addAttribute("branchId", id);
        model.addAttribute("activePage", "branches");
        return "branch/edit";
    }

    @GetMapping("/view/{id}")
    public String viewBranch(@PathVariable Long id, Model model) {
        BranchResponse branch = branchService.getBranchById(id);
        model.addAttribute("branch", branch);
        model.addAttribute("activePage", "branches");
        return "branch/view";
    }

    @PostMapping("/edit/{id}")
    public String updateBranch(
            @PathVariable Long id,
            @Valid @ModelAttribute("branchForm") BranchUpdateRequest branchForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("branchId", id);
            model.addAttribute("activePage", "branches");
            return "branch/edit";
        }

        try {
            branchService.updateBranch(id, branchForm);
            redirectAttributes.addFlashAttribute("successMessage", "Branch updated successfully!");
            return "redirect:/branches";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("branchId", id);
            model.addAttribute("activePage", "branches");
            return "branch/edit";
        }
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            RedirectAttributes redirectAttributes) {
        branchService.toggleBranchStatus(id, active);
        redirectAttributes.addFlashAttribute("successMessage", 
                active ? "Branch successfully activated!" : "Branch successfully deactivated!");
        return "redirect:/branches";
    }

    @PostMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            branchService.deleteBranch(id);
            redirectAttributes.addFlashAttribute("successMessage", "Branch deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/branches";
    }
}
