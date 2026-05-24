package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.dto.AnalysisForm;
import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.service.AnalysisService;
import com.pepitobuscaerror.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnalysisController {

	private final CompanyService companyService;
	private final AnalysisService analysisService;

	public AnalysisController(CompanyService companyService, AnalysisService analysisService) {
		this.companyService = companyService;
		this.analysisService = analysisService;
	}

	@GetMapping("/analyses")
	public String listAnalyses(Model model) {
		model.addAttribute("analyses", analysisService.findAllAnalyses());
		return "analyses/list";
	}

	@GetMapping("/analyses/new")
	public String chooseCompanyForNewAnalysis(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("infoMessage", "Choose a company before creating an analysis.");
		return "redirect:/companies";
	}

	@GetMapping("/companies/{companyId}/analyses/new")
	public String newAnalysis(@PathVariable Long companyId, Model model) {
		model.addAttribute("company", companyService.getCompany(companyId));
		model.addAttribute("analysisForm", new AnalysisForm());
		model.addAttribute("indicatorOptions", analysisService.availableIndicatorOptions());
		return "analyses/form";
	}

	@PostMapping("/companies/{companyId}/analyses/save")
	public String saveAnalysis(@PathVariable Long companyId,
			@ModelAttribute("analysisForm") AnalysisForm analysisForm, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("company", companyService.getCompany(companyId));
			model.addAttribute("indicatorOptions", analysisService.availableIndicatorOptions());
			return "analyses/form";
		}
		try {
			Analysis analysis = analysisService.createAnalysis(companyId, analysisForm);
			redirectAttributes.addFlashAttribute("successMessage", "Analysis completed successfully.");
			return "redirect:/analyses/" + analysis.getIdAnalysis();
		} catch (IllegalArgumentException exception) {
			bindingResult.reject("analysis.invalid", exception.getMessage());
			model.addAttribute("company", companyService.getCompany(companyId));
			model.addAttribute("indicatorOptions", analysisService.availableIndicatorOptions());
			return "analyses/form";
		}
	}

	@GetMapping("/analyses/{id}")
	public String analysisResult(@PathVariable Long id, Model model) {
		Analysis analysis = analysisService.getAnalysis(id);
		model.addAttribute("analysis", analysis);
		model.addAttribute("indicatorGroups", analysisService.groupIndicatorsBySeverity(analysis));
		model.addAttribute("recommendationGroups", analysisService.groupRecommendationsByPriority(analysis));
		return "analyses/result";
	}
}
