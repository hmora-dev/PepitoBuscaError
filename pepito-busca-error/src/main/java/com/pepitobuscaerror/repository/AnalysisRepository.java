package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.RiskLevel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

	@EntityGraph(attributePaths = "company")
	Optional<Analysis> findByIdAnalysis(Long idAnalysis);

	@EntityGraph(attributePaths = "company")
	List<Analysis> findTop8ByOrderByAnalysisDateDesc();

	long countByRiskLevel(RiskLevel riskLevel);

	@Query("select coalesce(avg(a.riskScore), 0) from Analysis a")
	double averageRiskScore();
}
