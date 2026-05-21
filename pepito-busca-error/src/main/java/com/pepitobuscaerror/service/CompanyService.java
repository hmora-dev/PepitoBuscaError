package com.pepitobuscaerror.service;

import com.pepitobuscaerror.model.Analysis;
import com.pepitobuscaerror.model.Company;
import com.pepitobuscaerror.repository.CompanyRepository;
import com.pepitobuscaerror.util.TargetNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

	private final CompanyRepository companyRepository;

	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	@Transactional(readOnly = true)
	public List<Company> findCompanies(String query) {
		List<Company> companies;
		if (query == null || query.isBlank()) {
			companies = companyRepository.findAllByOrderByRegistrationDateDesc();
		} else {
			String term = query.trim();
			companies = companyRepository
					.findByNameContainingIgnoreCaseOrDomainContainingIgnoreCaseOrSectorContainingIgnoreCaseOrderByRegistrationDateDesc(
							term, term, term);
		}
		companies.forEach(this::initializeCompanyDashboardData);
		return companies;
	}

	@Transactional(readOnly = true)
	public Company getCompany(Long id) {
		Company company = companyRepository.findByIdCompany(id)
				.orElseThrow(() -> new ResourceNotFoundException("The requested company does not exist."));
		initializeCompanyDashboardData(company);
		return company;
	}

	@Transactional
	public Company createCompany(Company company) {
		normalizeCompany(company);
		return companyRepository.save(company);
	}

	@Transactional
	public Company updateCompany(Long id, Company form) {
		Company company = companyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("The requested company does not exist."));
		company.setName(clean(form.getName()));
		company.setDomain(form.getDomain());
		company.setCorporateEmail(clean(form.getCorporateEmail()));
		company.setSector(clean(form.getSector()));
		normalizeCompany(company);
		return companyRepository.save(company);
	}

	@Transactional
	public void deleteCompany(Long id) {
		if (!companyRepository.existsById(id)) {
			throw new ResourceNotFoundException("The company you tried to delete does not exist.");
		}
		companyRepository.deleteById(id);
	}

	private void normalizeCompany(Company company) {
		TargetNormalizer.NormalizedTarget normalized = TargetNormalizer.normalize(company.getDomain(), company.getName());
		company.setName(clean(company.getName()));
		company.setDomain(normalized.domain());
		company.setCorporateEmail(clean(company.getCorporateEmail()));
		company.setSector(clean(company.getSector()));
	}

	private String clean(String value) {
		return value == null ? null : value.trim();
	}

	private void initializeCompanyDashboardData(Company company) {
		Analysis latestAnalysis = company.getLatestAnalysis();
		if (latestAnalysis != null) {
			latestAnalysis.getIndicators().size();
			latestAnalysis.getRecommendations().size();
		}
	}
}
