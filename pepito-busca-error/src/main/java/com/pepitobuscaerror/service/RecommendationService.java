package com.pepitobuscaerror.service;

import com.pepitobuscaerror.model.Priority;
import com.pepitobuscaerror.model.Recommendation;
import com.pepitobuscaerror.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

	private final RecommendationRepository recommendationRepository;

	public RecommendationService(RecommendationRepository recommendationRepository) {
		this.recommendationRepository = recommendationRepository;
	}

	@Transactional(readOnly = true)
	public List<Recommendation> findAllRecommendations() {
		return recommendationRepository.findAllByOrderByPriorityDescDescriptionAsc().stream()
				.sorted(Comparator.comparing((Recommendation recommendation) -> recommendation.getPriority().ordinal())
						.reversed()
						.thenComparing(Recommendation::getDescription))
				.toList();
	}

	@Transactional(readOnly = true)
	public Map<Priority, Long> countByPriority() {
		Map<Priority, Long> counts = new EnumMap<>(Priority.class);
		for (Priority priority : Priority.values()) {
			counts.put(priority, recommendationRepository.countByPriority(priority));
		}
		return counts;
	}
}
