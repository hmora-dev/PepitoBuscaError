package com.pepitobuscaerror.dto;

public class SecurityTrailsRecord {

	private final String type;
	private final String name;
	private final String value;
	private final String ttl;

	public SecurityTrailsRecord(String type, String name, String value, String ttl) {
		this.type = valueOrEmpty(type);
		this.name = valueOrEmpty(name);
		this.value = valueOrEmpty(value);
		this.ttl = valueOrEmpty(ttl);
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getTtl() {
		return ttl;
	}

	private String valueOrEmpty(String value) {
		return value == null ? "" : value;
	}
}
