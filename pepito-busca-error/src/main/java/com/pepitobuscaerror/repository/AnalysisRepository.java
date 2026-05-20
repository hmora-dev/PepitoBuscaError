package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.RiskLevel;
import com.pepitobuscaerror.model.Severity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

	@EntityGraph(attributePaths = "company")
	Optional<Analysis> findByIdAnalysis(Long idAnalysis);

	@EntityGraph(attributePaths = "company")
	List<Analysis> findTop8ByOrderByAnalysisDateDesc();

	@EntityGraph(attributePaths = "company")
	List<Analysis> findAllByOrderByAnalysisDateDesc();

	@EntityGraph(attributePaths = "company")
	Optional<Analysis> findFirstByRiskLevelInOrderByAnalysisDateDesc(Collection<RiskLevel> riskLevels);

	long countByRiskLevel(RiskLevel riskLevel);

	@Query("select coalesce(avg(a.riskScore), 0) from Analysis a")
	double averageRiskScore();

	@Query("select count(i) from Indicator i where i.severity = :severity")
	long countIndicatorsBySeverity(@Param("severity") Severity severity);
}
