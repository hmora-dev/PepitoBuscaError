package com.pepitobuscaerror.service;

import com.pepitobuscaerror.model.TrackedDevice;
import com.pepitobuscaerror.repository.TrackedDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrackedDeviceService {

	private static final int MAX_LOCATION_LABEL_LENGTH = 180;
	private static final int MAX_CLIENT_IP_LENGTH = 45;
	private static final int MAX_USER_AGENT_LENGTH = 255;

	private final TrackedDeviceRepository trackedDeviceRepository;

	public TrackedDeviceService(TrackedDeviceRepository trackedDeviceRepository) {
		this.trackedDeviceRepository = trackedDeviceRepository;
	}

	@Transactional
	public List<TrackedDevice> findDevices(String query) {
		List<TrackedDevice> devices;
		if (query == null || query.isBlank()) {
			devices = trackedDeviceRepository.findAllByOrderByLastSeenAtDesc();
		} else {
			String term = query.trim();
			devices = trackedDeviceRepository
					.findByNameContainingIgnoreCaseOrDeviceTypeContainingIgnoreCaseOrOwnerContainingIgnoreCaseOrLocationLabelContainingIgnoreCaseOrderByLastSeenAtDesc(
							term, term, term, term);
		}
		devices.forEach(this::ensureTrackingToken);
		return devices;
	}

	@Transactional
	public TrackedDevice getDevice(Long id) {
		TrackedDevice device = trackedDeviceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("The requested device does not exist."));
		ensureTrackingToken(device);
		return device;
	}

	@Transactional
	public TrackedDevice getDeviceByTrackingToken(String trackingToken) {
		TrackedDevice device = trackedDeviceRepository.findByTrackingToken(trackingToken)
				.orElseThrow(() -> new ResourceNotFoundException("The requested tracking link does not exist."));
		ensureTrackingToken(device);
		return device;
	}

	@Transactional(readOnly = true)
	public long countActiveDevices() {
		return trackedDeviceRepository.countByActiveTrue();
	}

	@Transactional(readOnly = true)
	public long countLocatedDevices() {
		return trackedDeviceRepository.countByLatitudeIsNotNullAndLongitudeIsNotNull();
	}

	@Transactional
	public TrackedDevice createDevice(TrackedDevice device) {
		cleanDevice(device);
		device.ensureTrackingToken();
		return trackedDeviceRepository.save(device);
	}

	@Transactional
	public TrackedDevice updateDevice(Long id, TrackedDevice form) {
		TrackedDevice device = getDevice(id);
		device.setName(clean(form.getName()));
		device.setDeviceType(clean(form.getDeviceType()));
		device.setOwner(clean(form.getOwner()));
		device.setNotes(clean(form.getNotes()));
		device.setActive(form.isActive());
		return trackedDeviceRepository.save(device);
	}

	@Transactional
	public TrackedDevice updateLivePosition(String trackingToken, Double latitude, Double longitude, Double accuracyMeters,
			String locationLabel, String clientIp, String userAgent) {
		validateCoordinates(latitude, longitude);
		TrackedDevice device = getDeviceByTrackingToken(trackingToken);
		if (!device.isActive()) {
			throw new IllegalArgumentException("This device is inactive.");
		}
		device.updatePosition(latitude, longitude, cleanAccuracy(accuracyMeters));
		device.updateClientMetadata(cleanWithMax(clientIp, MAX_CLIENT_IP_LENGTH),
				cleanWithMax(userAgent, MAX_USER_AGENT_LENGTH));
		String cleanLocationLabel = cleanLocationLabel(locationLabel);
		if (cleanLocationLabel != null) {
			device.setLocationLabel(cleanLocationLabel);
		}
		return trackedDeviceRepository.save(device);
	}

	@Transactional
	public void deleteDevice(Long id) {
		if (!trackedDeviceRepository.existsById(id)) {
			throw new ResourceNotFoundException("The device you tried to delete does not exist.");
		}
		trackedDeviceRepository.deleteById(id);
	}

	private void cleanDevice(TrackedDevice device) {
		device.setName(clean(device.getName()));
		device.setDeviceType(clean(device.getDeviceType()));
		device.setOwner(clean(device.getOwner()));
		device.setLocationLabel(clean(device.getLocationLabel()));
		device.setNotes(clean(device.getNotes()));
		device.setLastClientIp(null);
		device.setLastUserAgent(null);
	}

	private void ensureTrackingToken(TrackedDevice device) {
		if (device.getTrackingToken() == null || device.getTrackingToken().isBlank()) {
			device.ensureTrackingToken();
			trackedDeviceRepository.save(device);
		}
	}

	private void validateCoordinates(Double latitude, Double longitude) {
		if (latitude == null || longitude == null) {
			throw new IllegalArgumentException("Latitude and longitude are required.");
		}
		if (!Double.isFinite(latitude)) {
			throw new IllegalArgumentException("Latitude must be a finite number.");
		}
		if (!Double.isFinite(longitude)) {
			throw new IllegalArgumentException("Longitude must be a finite number.");
		}
		if (latitude < -90 || latitude > 90) {
			throw new IllegalArgumentException("Latitude must be between -90 and 90.");
		}
		if (longitude < -180 || longitude > 180) {
			throw new IllegalArgumentException("Longitude must be between -180 and 180.");
		}
	}

	private Double cleanAccuracy(Double accuracyMeters) {
		if (accuracyMeters == null) {
			return null;
		}
		if (!Double.isFinite(accuracyMeters) || accuracyMeters < 0) {
			throw new IllegalArgumentException("Accuracy must be a non-negative finite number.");
		}
		return accuracyMeters;
	}

	private String clean(String value) {
		return value == null ? null : value.trim();
	}

	private String cleanLocationLabel(String value) {
		String cleanValue = clean(value);
		if (cleanValue == null || cleanValue.isBlank()) {
			return null;
		}
		if (cleanValue.length() <= MAX_LOCATION_LABEL_LENGTH) {
			return cleanValue;
		}
		return cleanValue.substring(0, MAX_LOCATION_LABEL_LENGTH - 3) + "...";
	}

	private String cleanWithMax(String value, int maxLength) {
		String cleanValue = clean(value);
		if (cleanValue == null || cleanValue.isBlank()) {
			return null;
		}
		if (cleanValue.length() <= maxLength) {
			return cleanValue;
		}
		return cleanValue.substring(0, maxLength);
	}
}
