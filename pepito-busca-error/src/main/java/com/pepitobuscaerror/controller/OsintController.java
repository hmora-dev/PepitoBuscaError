package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.dto.OsintDomainForm;
import com.pepitobuscaerror.dto.OsintEmailForm;
import com.pepitobuscaerror.dto.TargetForm;
import com.pepitobuscaerror.model.ScanRun;
import com.pepitobuscaerror.service.AuditService;
import com.pepitobuscaerror.service.OsintService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/osint")
public class OsintController {

	private final AuditService auditService;
	private final OsintService osintService;

	public OsintController(AuditService auditService, OsintService osintService) {
		this.auditService = auditService;
		this.osintService = osintService;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String domain, @RequestParam(required = false) String name,
			Model model) {
		if (!model.containsAttribute("targetForm")) {
			TargetForm targetForm = new TargetForm();
			targetForm.setDomainOrUrl(domain);
			targetForm.setName(name);
			model.addAttribute("targetForm", targetForm);
		}
		if (!model.containsAttribute("osintDomainForm")) {
			OsintDomainForm domainForm = new OsintDomainForm();
			domainForm.setDomain(domain);
			model.addAttribute("osintDomainForm", domainForm);
		}
		if (!model.containsAttribute("osintEmailForm")) {
			model.addAttribute("osintEmailForm", new OsintEmailForm());
		}
		addIndexData(model);
		return "osint/index";
	}

	@PostMapping("/domain")
	public String analyzeDomain(@Valid @ModelAttribute("osintDomainForm") OsintDomainForm domainForm,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			addMissingIndexForms(model);
			addIndexData(model);
			return "osint/index";
		}
		model.addAttribute("result", osintService.analyzeDomain(domainForm.getDomain()));
		return "osint/domain-result";
	}

	@PostMapping("/email")
	public String analyzeEmail(@Valid @ModelAttribute("osintEmailForm") OsintEmailForm emailForm,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			addMissingIndexForms(model);
			addIndexData(model);
			return "osint/index";
		}
		model.addAttribute("result", osintService.checkEmail(emailForm.getEmail()));
		return "osint/email-result";
	}

	@PostMapping("/run")
	public String run(@Valid @ModelAttribute("targetForm") TargetForm targetForm, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			addMissingIndexForms(model);
			addIndexData(model);
			return "osint/index";
		}
		try {
			ScanRun scan = auditService.createAndRun(targetForm);
			redirectAttributes.addFlashAttribute("successMessage", "OSINT report completed.");
			return "redirect:/osint/scans/" + scan.getId();
		} catch (IllegalArgumentException exception) {
			bindingResult.reject("target.invalid", exception.getMessage());
			addMissingIndexForms(model);
			addIndexData(model);
			return "osint/index";
		}
	}

	@GetMapping("/scans/{id}")
	public String detail(@PathVariable Long id, Model model) {
		ScanRun scan = auditService.getScan(id);
		model.addAttribute("scan", scan);
		model.addAttribute("findings", auditService.sortFindings(scan.getFindings()));
		model.addAttribute("priorityFindings", auditService.priorityFindings(scan.getFindings()));
		model.addAttribute("severityCounts", auditService.countBySeverity(scan.getFindings()));
		model.addAttribute("categoryCounts", auditService.countByCategory(scan.getFindings()));
		return "osint/detail";
	}

	private void addIndexData(Model model) {
		model.addAttribute("recentScans", auditService.recentScans());
		model.addAttribute("recentTargets", auditService.recentTargets());
	}

	private void addMissingIndexForms(Model model) {
		if (!model.containsAttribute("targetForm")) {
			model.addAttribute("targetForm", new TargetForm());
		}
		if (!model.containsAttribute("osintDomainForm")) {
			model.addAttribute("osintDomainForm", new OsintDomainForm());
		}
		if (!model.containsAttribute("osintEmailForm")) {
			model.addAttribute("osintEmailForm", new OsintEmailForm());
		}
	}
}
