package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.service.IndicatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndicatorController {

	private final IndicatorService indicatorService;

	public IndicatorController(IndicatorService indicatorService) {
		this.indicatorService = indicatorService;
	}

	@GetMapping("/indicators")
	public String indicators(Model model) {
		model.addAttribute("indicators", indicatorService.findAllIndicators());
		model.addAttribute("indicatorCatalog", indicatorService.findIndicatorCatalog());
		model.addAttribute("severityCounts", indicatorService.countBySeverity());
		return "indicators/list";
	}
}
