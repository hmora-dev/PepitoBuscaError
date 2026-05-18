package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.AuditTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditTargetRepository extends JpaRepository<AuditTarget, Long> {
	Optional<AuditTarget> findFirstByDomainIgnoreCase(String domain);

	List<AuditTarget> findTop8ByOrderByCreatedAtDesc();
}
