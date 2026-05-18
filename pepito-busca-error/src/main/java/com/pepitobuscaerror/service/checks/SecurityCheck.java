package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;

import java.util.List;

public interface SecurityCheck {
	List<Finding> analyze(AuditTarget target);
}
