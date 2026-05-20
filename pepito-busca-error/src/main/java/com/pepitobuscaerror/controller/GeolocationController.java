package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.model.TrackedDevice;
import com.pepitobuscaerror.service.TrackingLinkService;
import com.pepitobuscaerror.service.TrackedDeviceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/geolocation")
public class GeolocationController {

	private final TrackedDeviceService trackedDeviceService;
	private final TrackingLinkService trackingLinkService;

	public GeolocationController(TrackedDeviceService trackedDeviceService, TrackingLinkService trackingLinkService) {
		this.trackedDeviceService = trackedDeviceService;
		this.trackingLinkService = trackingLinkService;
	}

	@GetMapping
	public String listDevices(@RequestParam(required = false) String q, Model model) {
		model.addAttribute("devices", trackedDeviceService.findDevices(q));
		model.addAttribute("query", q == null ? "" : q);
		model.addAttribute("activeDevices", trackedDeviceService.countActiveDevices());
		return "geolocation/list";
	}

	@GetMapping("/new")
	public String newDevice(Model model) {
		TrackedDevice device = new TrackedDevice();
		device.setActive(true);
		model.addAttribute("device", device);
		model.addAttribute("pageTitle", "Register device");
		return "geolocation/form";
	}

	@PostMapping("/save")
	public String saveDevice(@Valid @ModelAttribute("device") TrackedDevice device, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "Register device");
			return "geolocation/form";
		}
		TrackedDevice savedDevice = trackedDeviceService.createDevice(device);
		redirectAttributes.addFlashAttribute("successMessage", "Device registered successfully.");
		return "redirect:/geolocation/" + savedDevice.getIdDevice();
	}

	@GetMapping("/{id}")
	public String deviceDetail(@PathVariable Long id, HttpServletRequest request, Model model) {
		TrackedDevice device = trackedDeviceService.getDevice(id);
		TrackingLinkService.TrackingLinks trackingLinks =
				trackingLinkService.buildLinks(request, device.getTrackingToken());
		model.addAttribute("device", device);
		model.addAttribute("trackingLinks", trackingLinks);
		return "geolocation/detail";
	}

	@GetMapping("/{id}/position")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> currentPosition(@PathVariable Long id) {
		TrackedDevice device = trackedDeviceService.getDevice(id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", "ok");
		body.put("deviceId", device.getIdDevice());
		body.put("active", device.isActive());
		body.put("hasCoordinates", device.hasCoordinates());
		body.put("latitude", device.getLatitude());
		body.put("longitude", device.getLongitude());
		body.put("accuracy", device.getAccuracyMeters());
		body.put("locationLabel", device.getLocationLabel());
		body.put("lastSeenAt", device.getLastSeenAt().toString());
		return ResponseEntity.ok(body);
	}

	@GetMapping("/live/{trackingToken}")
	public String liveTracking(@PathVariable String trackingToken, Model model) {
		model.addAttribute("device", trackedDeviceService.getDeviceByTrackingToken(trackingToken));
		return "geolocation/live";
	}

	@PostMapping("/live/{trackingToken}/position")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateLivePosition(@PathVariable String trackingToken,
			@RequestParam Double latitude, @RequestParam Double longitude,
			@RequestParam(required = false) Double accuracy,
			@RequestParam(required = false) String locationLabel) {
		try {
			TrackedDevice device = trackedDeviceService.updateLivePosition(trackingToken, latitude, longitude, accuracy,
					locationLabel);
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("status", "ok");
			body.put("deviceId", device.getIdDevice());
			body.put("latitude", device.getLatitude());
			body.put("longitude", device.getLongitude());
			body.put("accuracy", device.getAccuracyMeters() == null ? 0 : device.getAccuracyMeters());
			body.put("locationLabel", device.getLocationLabel());
			body.put("lastSeenAt", device.getLastSeenAt().toString());
			return ResponseEntity.ok(body);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("status", "error", "message", exception.getMessage()));
		}
	}

	@GetMapping("/edit/{id}")
	public String editDevice(@PathVariable Long id, Model model) {
		model.addAttribute("device", trackedDeviceService.getDevice(id));
		model.addAttribute("pageTitle", "Edit device");
		return "geolocation/form";
	}

	@PostMapping("/update/{id}")
	public String updateDevice(@PathVariable Long id, @Valid @ModelAttribute("device") TrackedDevice device,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		device.setIdDevice(id);
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "Edit device");
			return "geolocation/form";
		}
		TrackedDevice updatedDevice = trackedDeviceService.updateDevice(id, device);
		redirectAttributes.addFlashAttribute("successMessage", "Device updated successfully.");
		return "redirect:/geolocation/" + updatedDevice.getIdDevice();
	}

	@GetMapping("/delete/{id}")
	public String deleteDevice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		trackedDeviceService.deleteDevice(id);
		redirectAttributes.addFlashAttribute("successMessage", "Device deleted successfully.");
		return "redirect:/geolocation";
	}
}
