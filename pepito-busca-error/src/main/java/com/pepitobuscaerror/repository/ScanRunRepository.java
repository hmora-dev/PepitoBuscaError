package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.model.ScanRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScanRunRepository extends JpaRepository<ScanRun, Long> {

	List<ScanRun> findTop12ByOrderByStartedAtDesc();

	List<ScanRun> findByTarget_IdOrderByStartedAtDesc(Long targetId);

	@Query("select count(f) from Finding f where f.severity = :severity")
	long countFindingsBySeverity(@Param("severity") FindingSeverity severity);

	@Query("select f.category, count(f) from Finding f group by f.category")
	List<Object[]> countFindingsByCategory();

	default long countFindingsByCategory(FindingCategory category) {
		return countFindingsByCategory().stream()
				.filter(row -> row[0] == category)
				.mapToLong(row -> (Long) row[1])
				.sum();
	}
}
