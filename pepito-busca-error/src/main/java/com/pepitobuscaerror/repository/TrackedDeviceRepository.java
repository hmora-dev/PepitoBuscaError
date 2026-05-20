package com.pepitobuscaerror.repository;

import com.pepitobuscaerror.model.TrackedDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackedDeviceRepository extends JpaRepository<TrackedDevice, Long> {

	List<TrackedDevice> findAllByOrderByLastSeenAtDesc();

	List<TrackedDevice> findByNameContainingIgnoreCaseOrDeviceTypeContainingIgnoreCaseOrOwnerContainingIgnoreCaseOrLocationLabelContainingIgnoreCaseOrderByLastSeenAtDesc(
			String name, String deviceType, String owner, String locationLabel);

	Optional<TrackedDevice> findByTrackingToken(String trackingToken);

	long countByActiveTrue();
}
