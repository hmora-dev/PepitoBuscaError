package com.pepitobuscaerror.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class OsintDomainForm {

	@NotBlank(message = "Enter a domain")
	@Size(max = 253, message = "Domain is too long")
	@Pattern(regexp = "^(?=.{1,253}$)(?!-)(?:[A-Za-z0-9-]{1,63}\\.)+[A-Za-z]{2,63}$",
			message = "Enter a valid domain such as example.com")
	private String domain;

	@AssertTrue(message = "Confirm that you are authorized to review this domain")
	private boolean authorized;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain == null ? null : domain.trim().toLowerCase();
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
}
