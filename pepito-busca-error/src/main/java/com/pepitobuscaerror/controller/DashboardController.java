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

@Controller
public class DashboardController {

	private final AuditService auditService;

	public DashboardController(AuditService auditService) {
		this.auditService = auditService;
	}

	@GetMapping("/")
	public String dashboard(Model model) {
		addDashboardModel(model, new TargetForm());
		return "dashboard";
	}

	@PostMapping("/scans")
	public String runScan(@Valid @ModelAttribute("targetForm") TargetForm targetForm,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			addDashboardModel(model, targetForm);
			return "dashboard";
		}

		try {
			ScanRun scanRun = auditService.createAndRun(targetForm);
			return "redirect:/scans/" + scanRun.getId();
		} catch (IllegalArgumentException exception) {
			bindingResult.rejectValue("domainOrUrl", "target.invalid", exception.getMessage());
			addDashboardModel(model, targetForm);
			return "dashboard";
		}
	}

	@GetMapping("/scans/{id}")
	public String scanDetail(@PathVariable Long id, Model model) {
		ScanRun scanRun = auditService.getScan(id);
		model.addAttribute("scan", scanRun);
		model.addAttribute("findings", auditService.sortFindings(scanRun.getFindings()));
		model.addAttribute("severityCounts", auditService.countBySeverity(scanRun.getFindings()));
		return "scan-detail";
	}

	private void addDashboardModel(Model model, TargetForm targetForm) {
		model.addAttribute("targetForm", targetForm);
		model.addAttribute("recentScans", auditService.recentScans());
		model.addAttribute("recentTargets", auditService.recentTargets());
	}
}
