package com.pepitobuscaerror.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GeolocationSchemaService {

	private final JdbcTemplate jdbcTemplate;

	public GeolocationSchemaService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void makeCoordinatesOptional() {
		try {
			jdbcTemplate.execute("alter table tracked_devices modify column latitude double null");
			jdbcTemplate.execute("alter table tracked_devices modify column longitude double null");
		} catch (DataAccessException exception) {
			// Hibernate creates the correct schema for new databases. This only upgrades older MySQL tables.
		}
	}
}
