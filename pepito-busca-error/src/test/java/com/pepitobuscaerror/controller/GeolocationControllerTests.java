package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.model.TrackedDevice;
import com.pepitobuscaerror.service.TrackedDeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GeolocationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TrackedDeviceService trackedDeviceService;

	@Test
	void livePositionUpdateStoresCoordinates() throws Exception {
		TrackedDevice savedDevice = trackedDeviceService.createDevice(activeDevice("Field phone"));

		mockMvc.perform(post("/geolocation/live/{token}/position", savedDevice.getTrackingToken())
						.param("latitude", "40.4168")
						.param("longitude", "-3.7038")
						.param("accuracy", "12.5")
						.param("locationLabel", "Madrid, Spain")
						.header("X-Forwarded-For", "203.0.113.42, 10.0.0.5")
						.header("User-Agent", "JUnit browser"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.latitude").value(40.4168))
				.andExpect(jsonPath("$.longitude").value(-3.7038))
				.andExpect(jsonPath("$.accuracy").value(12.5))
				.andExpect(jsonPath("$.locationLabel").value("Madrid, Spain"))
				.andExpect(jsonPath("$.lastClientIp").value("203.0.113.42"))
				.andExpect(jsonPath("$.lastUserAgent").value("JUnit browser"));

		TrackedDevice updatedDevice = trackedDeviceService.getDevice(savedDevice.getIdDevice());
		assertThat(updatedDevice.getLatitude()).isEqualTo(40.4168);
		assertThat(updatedDevice.getLongitude()).isEqualTo(-3.7038);
		assertThat(updatedDevice.getAccuracyMeters()).isEqualTo(12.5);
		assertThat(updatedDevice.getLocationLabel()).isEqualTo("Madrid, Spain");
		assertThat(updatedDevice.getLastClientIp()).isEqualTo("203.0.113.42");
		assertThat(updatedDevice.getLastUserAgent()).isEqualTo("JUnit browser");
	}

	@Test
	void inactiveDeviceRejectsLivePositionUpdates() throws Exception {
		TrackedDevice device = activeDevice("Inactive phone");
		device.setActive(false);
		TrackedDevice savedDevice = trackedDeviceService.createDevice(device);

		mockMvc.perform(post("/geolocation/live/{token}/position", savedDevice.getTrackingToken())
						.param("latitude", "40.4168")
						.param("longitude", "-3.7038"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("This device is inactive."));
	}

	@Test
	void invalidCoordinatesAreRejected() throws Exception {
		TrackedDevice savedDevice = trackedDeviceService.createDevice(activeDevice("Invalid phone"));

		mockMvc.perform(post("/geolocation/live/{token}/position", savedDevice.getTrackingToken())
						.param("latitude", "95")
						.param("longitude", "-3.7038"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Latitude must be between -90 and 90."));
	}

	private TrackedDevice activeDevice(String name) {
		TrackedDevice device = new TrackedDevice();
		device.setName(name);
		device.setDeviceType("Phone");
		device.setOwner("Analyst");
		device.setActive(true);
		return device;
	}
}
