package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

	@EntityGraph(attributePaths = "analyses")
	List<Company> findAllByOrderByRegistrationDateDesc();

	@EntityGraph(attributePaths = "analyses")
	List<Company> findTop5ByOrderByRegistrationDateDesc();

	@EntityGraph(attributePaths = "analyses")
	Optional<Company> findByIdCompany(Long idCompany);

	@EntityGraph(attributePaths = "analyses")
	List<Company> findByNameContainingIgnoreCaseOrDomainContainingIgnoreCaseOrSectorContainingIgnoreCaseOrderByRegistrationDateDesc(
			String name, String domain, String sector);
}
