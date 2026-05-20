package com.pepitobuscaerror.service.checks;

import com.pepitobuscaerror.dto.OsintDomainResult;
import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.service.OsintService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OsintProviderCheck implements SecurityCheck {

	private final OsintService osintService;

	public OsintProviderCheck(OsintService osintService) {
		this.osintService = osintService;
	}

	@Override
	public List<Finding> analyze(AuditTarget target) {
		OsintDomainResult result = osintService.analyzeDomain(target.getDomain());
		return osintService.toFindings(result);
	}
}
