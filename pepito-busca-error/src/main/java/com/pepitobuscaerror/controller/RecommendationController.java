package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.service.RecommendationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RecommendationController {

	private final RecommendationService recommendationService;

	public RecommendationController(RecommendationService recommendationService) {
		this.recommendationService = recommendationService;
	}

	@GetMapping("/recommendations")
	public String recommendations(Model model) {
		model.addAttribute("recommendations", recommendationService.findAllRecommendations());
		model.addAttribute("priorityCounts", recommendationService.countByPriority());
		return "recommendations/list";
	}
}
