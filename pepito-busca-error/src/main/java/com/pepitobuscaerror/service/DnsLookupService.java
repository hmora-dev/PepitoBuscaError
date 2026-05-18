package com.pepitobuscaerror.service;

import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

@Service
public class DnsLookupService {

	public List<String> lookup(String domain, String recordType) {
		Hashtable<String, String> environment = new Hashtable<>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
		environment.put("com.sun.jndi.dns.timeout.initial", "3000");
		environment.put("com.sun.jndi.dns.timeout.retries", "1");

		try {
			DirContext context = new InitialDirContext(environment);
			Attributes attributes = context.getAttributes(domain, new String[] { recordType });
			Attribute attribute = attributes.get(recordType);
			if (attribute == null) {
				return List.of();
			}

			List<String> records = new ArrayList<>();
			NamingEnumeration<?> values = attribute.getAll();
			while (values.hasMore()) {
				records.add(clean(values.next().toString()));
			}
			return records;
		} catch (NamingException exception) {
			return List.of();
		}
	}

	public List<String> lookupTxtStartingWith(String domain, String prefix) {
		String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);
		return lookup(domain, "TXT").stream()
				.filter(record -> record.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix))
				.toList();
	}

	private String clean(String record) {
		String trimmed = record.trim();
		if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() > 1) {
			trimmed = trimmed.substring(1, trimmed.length() - 1);
		}
		return trimmed.replace("\" \"", "");
	}
}
