package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.Indicator;
import com.pepitobuscaerror.model.Severity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndicatorRepository extends JpaRepository<Indicator, Long> {

	@EntityGraph(attributePaths = {"analysis", "analysis.company"})
	List<Indicator> findAllByOrderBySeverityDescTypeAsc();

	long countBySeverity(Severity severity);
}
