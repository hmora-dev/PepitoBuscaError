package com.pepitobuscaerror.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracked_devices")
public class TrackedDevice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_device")
	private Long idDevice;

	@NotBlank(message = "Device name is required")
	@Size(max = 120, message = "Device name is too long")
	@Column(nullable = false, length = 120)
	private String name;

	@NotBlank(message = "Device type is required")
	@Size(max = 80, message = "Device type is too long")
	@Column(name = "device_type", nullable = false, length = 80)
	private String deviceType;

	@NotBlank(message = "Owner is required")
	@Size(max = 120, message = "Owner name is too long")
	@Column(nullable = false, length = 120)
	private String owner;

	@DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
	@DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
	@Column
	private Double latitude;

	@DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
	@DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
	@Column
	private Double longitude;

	@Column(name = "accuracy_meters")
	private Double accuracyMeters;

	@Size(max = 180, message = "Location label is too long")
	@Column(name = "location_label", length = 180)
	private String locationLabel;

	@Size(max = 500, message = "Notes are too long")
	@Column(length = 500)
	private String notes;

	@Column(nullable = false)
	private boolean active = true;

	@Column(name = "tracking_token", unique = true, length = 80)
	private String trackingToken;

	@Column(name = "registered_at", nullable = false)
	private LocalDateTime registeredAt;

	@Column(name = "last_seen_at", nullable = false)
	private LocalDateTime lastSeenAt;

	@PrePersist
	void onCreate() {
		if (registeredAt == null) {
			registeredAt = LocalDateTime.now();
		}
		if (lastSeenAt == null) {
			lastSeenAt = LocalDateTime.now();
		}
		ensureTrackingToken();
	}

	@PreUpdate
	void onUpdate() {
		lastSeenAt = LocalDateTime.now();
		ensureTrackingToken();
	}

	public void ensureTrackingToken() {
		if (trackingToken == null || trackingToken.isBlank()) {
			trackingToken = UUID.randomUUID().toString();
		}
	}

	public void updatePosition(Double latitude, Double longitude, Double accuracyMeters) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracyMeters = accuracyMeters;
		this.lastSeenAt = LocalDateTime.now();
	}

	public Long getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Long idDevice) {
		this.idDevice = idDevice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAccuracyMeters() {
		return accuracyMeters;
	}

	public void setAccuracyMeters(Double accuracyMeters) {
		this.accuracyMeters = accuracyMeters;
	}

	public boolean hasCoordinates() {
		return latitude != null && longitude != null;
	}

	public String getLocationLabel() {
		return locationLabel;
	}

	public void setLocationLabel(String locationLabel) {
		this.locationLabel = locationLabel;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTrackingToken() {
		return trackingToken;
	}

	public void setTrackingToken(String trackingToken) {
		this.trackingToken = trackingToken;
	}

	public LocalDateTime getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(LocalDateTime registeredAt) {
		this.registeredAt = registeredAt;
	}

	public LocalDateTime getLastSeenAt() {
		return lastSeenAt;
	}

	public void setLastSeenAt(LocalDateTime lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}
}
