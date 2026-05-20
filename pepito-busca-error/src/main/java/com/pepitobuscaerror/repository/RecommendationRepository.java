package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.Priority;
import com.pepitobuscaerror.model.Recommendation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

	@EntityGraph(attributePaths = {"analysis", "analysis.company"})
	List<Recommendation> findAllByOrderByPriorityDescDescriptionAsc();

	long countByPriority(Priority priority);
}
