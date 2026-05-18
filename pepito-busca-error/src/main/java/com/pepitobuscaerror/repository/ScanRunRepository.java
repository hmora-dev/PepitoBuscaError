package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.ScanRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScanRunRepository extends JpaRepository<ScanRun, Long> {
	List<ScanRun> findTop12ByOrderByStartedAtDesc();
}
