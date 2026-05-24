package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.model.AuditTarget;
import com.pepitobuscaerror.model.Finding;
import com.pepitobuscaerror.model.FindingCategory;
import com.pepitobuscaerror.model.FindingSeverity;
import com.pepitobuscaerror.model.ScanRun;
import com.pepitobuscaerror.repository.AuditTargetRepository;
import com.pepitobuscaerror.repository.ScanRunRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OsintControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuditTargetRepository targetRepository;

	@Autowired
	private ScanRunRepository scanRunRepository;

	@Test
	void indexRenders() throws Exception {
		mockMvc.perform(get("/osint"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("OSINT Intelligence")));
	}

	@Test
	void domainResultRendersDemoProviders() throws Exception {
		mockMvc.perform(post("/osint/domain")
						.param("domain", "example.com")
						.param("authorized", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("DNSDumpster")))
				.andExpect(content().string(containsString("SecurityTrails API key not configured. Showing demo data.")));
	}

	@Test
	void emailResultRendersDemoProvider() throws Exception {
		mockMvc.perform(post("/osint/email")
						.param("email", "security@example.com")
						.param("authorized", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Have I Been Pwned")))
				.andExpect(content().string(containsString("HIBP API key not configured. Showing demo data.")));
	}

	@Test
	void invalidDomainReturnsFriendlyValidation() throws Exception {
		mockMvc.perform(post("/osint/domain")
						.param("domain", "not a valid domain")
						.param("authorized", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Enter a valid domain such as example.com")));
	}

	@Test
	void detailRendersSavedReport() throws Exception {
		AuditTarget target = targetRepository.save(new AuditTarget("Example", "example.com", "https://example.com"));
		ScanRun scanRun = new ScanRun(target);
		scanRun.addFinding(new Finding(
				FindingCategory.OSINT,
				FindingSeverity.INFO,
				"Inventory collected",
				"A records: 93.184.216.34",
				"Keep public records documented."
		));
		scanRun.complete(0);
		ScanRun saved = scanRunRepository.save(scanRun);

		mockMvc.perform(get("/osint/scans/{id}", saved.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Passive OSINT report")))
				.andExpect(content().string(containsString("Inventory collected")));
	}

	@Test
	void deleteReportRemovesSavedScan() throws Exception {
		AuditTarget target = targetRepository.save(new AuditTarget("Delete Report", "delete-report.example",
				"https://delete-report.example"));
		ScanRun scanRun = new ScanRun(target);
		scanRun.addFinding(new Finding(
				FindingCategory.OSINT,
				FindingSeverity.INFO,
				"Temporary report",
				"Evidence",
				"Recommendation"
		));
		scanRun.complete(0);
		ScanRun saved = scanRunRepository.save(scanRun);

		mockMvc.perform(post("/osint/scans/{id}/delete", saved.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/osint"));

		assertThat(scanRunRepository.findById(saved.getId())).isEmpty();
	}

	@Test
	void deleteTargetRemovesTargetAndItsReports() throws Exception {
		AuditTarget target = targetRepository.save(new AuditTarget("Delete Target", "delete-target.example",
				"https://delete-target.example"));
		ScanRun scanRun = new ScanRun(target);
		scanRun.addFinding(new Finding(
				FindingCategory.DNS,
				FindingSeverity.LOW,
				"Temporary target report",
				"Evidence",
				"Recommendation"
		));
		scanRun.complete(3);
		ScanRun saved = scanRunRepository.save(scanRun);

		mockMvc.perform(post("/osint/targets/{id}/delete", target.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/osint"));

		assertThat(targetRepository.findById(target.getId())).isEmpty();
		assertThat(scanRunRepository.findById(saved.getId())).isEmpty();
	}
}
