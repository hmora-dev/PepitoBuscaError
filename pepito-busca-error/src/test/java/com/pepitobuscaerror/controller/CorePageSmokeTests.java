package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.dto.AnalysisForm;
import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.Company;
import com.pepitobuscaerror.model.TrackedDevice;
import com.pepitobuscaerror.repository.CompanyRepository;
import com.pepitobuscaerror.service.AnalysisService;
import com.pepitobuscaerror.service.BasicRiskCalculator;
import com.pepitobuscaerror.service.TrackedDeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorePageSmokeTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private TrackedDeviceService trackedDeviceService;

	@Test
	void dashboardAndListPagesRender() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("PepitoBuscaError")));
		mockMvc.perform(get("/dashboard"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Security Overview")));
		mockMvc.perform(get("/companies"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Companies")));
		mockMvc.perform(get("/geolocation"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Geolocation")));
	}

	@Test
	void analysisShortcutRedirectsToCompanySelection() throws Exception {
		mockMvc.perform(get("/analyses/new"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/companies"))
				.andExpect(flash().attribute("infoMessage", "Choose a company before creating an analysis."));
	}

	@Test
	void invalidNumericRouteParameterRendersNotFound() throws Exception {
		mockMvc.perform(get("/analyses/not-a-number"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Page not found")));
	}

	@Test
	void companyDetailAndAnalysisResultRender() throws Exception {
		Company company = new Company();
		company.setName("Example Company");
		company.setDomain("example.com");
		company.setCorporateEmail("security@example.com");
		company.setSector("Technology");
		Company savedCompany = companyRepository.save(company);

		AnalysisForm form = new AnalysisForm();
		form.setSelectedIndicators(List.of(BasicRiskCalculator.DOMAIN_WITHOUT_HTTPS));
		Analysis analysis = analysisService.createAnalysis(savedCompany.getIdCompany(), form);

		mockMvc.perform(get("/companies/{id}", savedCompany.getIdCompany()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Risk trend")));
		mockMvc.perform(get("/analyses/{id}", analysis.getIdAnalysis()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Action plan")));
	}

	@Test
	void geolocationDetailAndLivePagesRender() throws Exception {
		TrackedDevice device = new TrackedDevice();
		device.setName("Demo phone");
		device.setDeviceType("Phone");
		device.setOwner("Analyst");
		device.setActive(true);
		TrackedDevice savedDevice = trackedDeviceService.createDevice(device);

		mockMvc.perform(get("/geolocation/{id}", savedDevice.getIdDevice()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Public tracking link")))
				.andExpect(content().string(containsString("/webjars/leaflet/1.9.4/dist/leaflet.js")));
		mockMvc.perform(get("/geolocation/live/{token}", savedDevice.getTrackingToken()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Allow location permission")))
				.andExpect(content().string(containsString("This page has no dashboard, menus, or map.")));
	}
}
