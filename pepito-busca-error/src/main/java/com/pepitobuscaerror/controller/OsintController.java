package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.dto.TargetForm;
import com.pepitobuscaerror.model.ScanRun;
import com.pepitobuscaerror.service.AuditService;
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

	public OsintController(AuditService auditService) {
		this.auditService = auditService;
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
		addIndexData(model);
		return "osint/index";
	}

	@PostMapping("/run")
	public String run(@Valid @ModelAttribute("targetForm") TargetForm targetForm, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			addIndexData(model);
			return "osint/index";
		}
		try {
			ScanRun scan = auditService.createAndRun(targetForm);
			redirectAttributes.addFlashAttribute("successMessage", "OSINT report completed.");
			return "redirect:/osint/scans/" + scan.getId();
		} catch (IllegalArgumentException exception) {
			bindingResult.reject("target.invalid", exception.getMessage());
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
}
