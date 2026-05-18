package com.pepitobuscaerror.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TargetForm {

	@Size(max = 140, message = "Name is too long")
	private String name;

	@NotBlank(message = "Enter a domain or URL")
	@Size(max = 500, message = "Domain or URL is too long")
	private String domainOrUrl;

	@AssertTrue(message = "Confirm that you are authorized to analyze this target")
	private boolean authorized;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomainOrUrl() {
		return domainOrUrl;
	}

	public void setDomainOrUrl(String domainOrUrl) {
		this.domainOrUrl = domainOrUrl;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
}
