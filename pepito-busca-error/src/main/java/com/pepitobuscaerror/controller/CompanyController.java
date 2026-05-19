package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.model.Company;
import com.pepitobuscaerror.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/companies")
public class CompanyController {

	private final CompanyService companyService;

	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping
	public String listCompanies(@RequestParam(required = false) String q, Model model) {
		model.addAttribute("companies", companyService.findCompanies(q));
		model.addAttribute("query", q == null ? "" : q);
		return "companies/list";
	}

	@GetMapping("/new")
	public String newCompany(Model model) {
		model.addAttribute("company", new Company());
		model.addAttribute("pageTitle", "Register company");
		return "companies/form";
	}

	@PostMapping("/save")
	public String saveCompany(@Valid @ModelAttribute("company") Company company, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "Register company");
			return "companies/form";
		}
		try {
			Company savedCompany = companyService.createCompany(company);
			redirectAttributes.addFlashAttribute("successMessage", "Company registered successfully.");
			return "redirect:/companies/" + savedCompany.getIdCompany();
		} catch (IllegalArgumentException exception) {
			bindingResult.rejectValue("domain", "domain.invalid", exception.getMessage());
			model.addAttribute("pageTitle", "Register company");
			return "companies/form";
		}
	}

	@GetMapping("/{id}")
	public String companyDetail(@PathVariable Long id, Model model) {
		model.addAttribute("company", companyService.getCompany(id));
		return "companies/detail";
	}

	@GetMapping("/edit/{id}")
	public String editCompany(@PathVariable Long id, Model model) {
		model.addAttribute("company", companyService.getCompany(id));
		model.addAttribute("pageTitle", "Edit company");
		return "companies/form";
	}

	@PostMapping("/update/{id}")
	public String updateCompany(@PathVariable Long id, @Valid @ModelAttribute("company") Company company,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		company.setIdCompany(id);
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "Edit company");
			return "companies/form";
		}
		try {
			Company updatedCompany = companyService.updateCompany(id, company);
			redirectAttributes.addFlashAttribute("successMessage", "Company updated successfully.");
			return "redirect:/companies/" + updatedCompany.getIdCompany();
		} catch (IllegalArgumentException exception) {
			bindingResult.rejectValue("domain", "domain.invalid", exception.getMessage());
			model.addAttribute("pageTitle", "Edit company");
			return "companies/form";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteCompany(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		companyService.deleteCompany(id);
		redirectAttributes.addFlashAttribute("successMessage", "Company deleted successfully.");
		return "redirect:/companies";
	}
}
