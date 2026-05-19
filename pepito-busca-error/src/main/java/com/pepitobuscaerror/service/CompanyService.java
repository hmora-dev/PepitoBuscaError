package com.pepitobuscaerror.service;

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
		if (query == null || query.isBlank()) {
			return companyRepository.findAllByOrderByRegistrationDateDesc();
		}
		String term = query.trim();
		return companyRepository
				.findByNameContainingIgnoreCaseOrDomainContainingIgnoreCaseOrSectorContainingIgnoreCaseOrderByRegistrationDateDesc(
						term, term, term);
	}

	@Transactional(readOnly = true)
	public Company getCompany(Long id) {
		return companyRepository.findByIdCompany(id)
				.orElseThrow(() -> new ResourceNotFoundException("The requested company does not exist."));
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
}
