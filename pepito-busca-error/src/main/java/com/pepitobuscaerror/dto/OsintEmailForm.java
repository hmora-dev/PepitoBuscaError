package com.pepitobuscaerror.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OsintEmailForm {

	@NotBlank(message = "Enter a corporate email")
	@Email(message = "Enter a valid email address")
	@Size(max = 254, message = "Email is too long")
	private String email;

	@AssertTrue(message = "Confirm that you are authorized to review this email")
	private boolean authorized;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim().toLowerCase();
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
}
