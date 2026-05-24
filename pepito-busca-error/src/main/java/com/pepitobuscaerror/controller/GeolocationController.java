package com.pepitobuscaerror.controller;

import com.pepitobuscaerror.dto.PublicLinkInfo;
import com.pepitobuscaerror.model.TrackedDevice;
import com.pepitobuscaerror.service.PublicLinkService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/geolocation")
public class GeolocationController {

	private final TrackedDeviceService trackedDeviceService;
	private final PublicLinkService publicLinkService;

	public GeolocationController(TrackedDeviceService trackedDeviceService, PublicLinkService publicLinkService) {
		this.trackedDeviceService = trackedDeviceService;
		this.publicLinkService = publicLinkService;
	}

	@GetMapping
	public String listDevices(@RequestParam(required = false) String q, Model model) {
		model.addAttribute("devices", trackedDeviceService.findDevices(q));
		model.addAttribute("query", q == null ? "" : q);
		model.addAttribute("activeDevices", trackedDeviceService.countActiveDevices());
		model.addAttribute("locatedDevices", trackedDeviceService.countLocatedDevices());
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
		PublicLinkInfo publicLinkInfo = publicLinkService.buildGpsLink(device.getTrackingToken(), request);
		model.addAttribute("device", device);
		model.addAttribute("publicLinkInfo", publicLinkInfo);
		model.addAttribute("publicClientLink", publicLinkInfo.getUrl());
		model.addAttribute("publicLinkStatus", publicLinkInfo.getStatusLabel());
		model.addAttribute("publicLinkWarning", publicLinkInfo.getWarningMessage());
		model.addAttribute("publicHttpsReady", publicLinkInfo.isPublicHttpsReady());
		model.addAttribute("usableFromAnotherNetwork", publicLinkInfo.isUsableFromAnotherNetwork());
		model.addAttribute("publicLinkReady", publicLinkInfo.isPublicHttpsReady());
		model.addAttribute("usableFromDifferentNetwork", publicLinkInfo.isUsableFromAnotherNetwork());
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
		body.put("lastClientIp", device.getLastClientIp());
		body.put("lastUserAgent", device.getLastUserAgent());
		body.put("trackingStatus", device.getTrackingStatusLabel());
		body.put("trackingStatusClass", device.getTrackingStatusClass());
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
			@RequestParam(required = false) String locationLabel,
			HttpServletRequest request) {
		try {
			TrackedDevice device = trackedDeviceService.updateLivePosition(trackingToken, latitude, longitude, accuracy,
					locationLabel, resolveClientIp(request), request.getHeader("User-Agent"));
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("status", "ok");
			body.put("deviceId", device.getIdDevice());
			body.put("latitude", device.getLatitude());
			body.put("longitude", device.getLongitude());
			body.put("accuracy", device.getAccuracyMeters() == null ? 0 : device.getAccuracyMeters());
			body.put("locationLabel", device.getLocationLabel());
			body.put("lastClientIp", device.getLastClientIp());
			body.put("lastUserAgent", device.getLastUserAgent());
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

	private String resolveClientIp(HttpServletRequest request) {
		for (String headerName : List.of("CF-Connecting-IP", "X-Real-IP", "X-Forwarded-For")) {
			String candidate = firstHeaderValue(request.getHeader(headerName));
			if (candidate != null) {
				return candidate;
			}
		}
		String forwardedCandidate = forwardedForValue(request.getHeader("Forwarded"));
		if (forwardedCandidate != null) {
			return forwardedCandidate;
		}
		return cleanIpCandidate(request.getRemoteAddr());
	}

	private String firstHeaderValue(String headerValue) {
		if (headerValue == null || headerValue.isBlank()) {
			return null;
		}
		return cleanIpCandidate(headerValue.split(",", 2)[0]);
	}

	private String forwardedForValue(String headerValue) {
		String firstForwardedEntry = firstHeaderValue(headerValue);
		if (firstForwardedEntry == null) {
			return null;
		}
		for (String part : firstForwardedEntry.split(";")) {
			String cleanPart = part.trim();
			if (cleanPart.regionMatches(true, 0, "for=", 0, 4)) {
				return cleanIpCandidate(cleanPart.substring(4));
			}
		}
		return null;
	}

	private String cleanIpCandidate(String value) {
		if (value == null) {
			return null;
		}
		String cleanValue = value.trim();
		if (cleanValue.isBlank() || "unknown".equalsIgnoreCase(cleanValue)) {
			return null;
		}
		if (cleanValue.startsWith("\"") && cleanValue.endsWith("\"") && cleanValue.length() > 1) {
			cleanValue = cleanValue.substring(1, cleanValue.length() - 1);
		}
		if (cleanValue.startsWith("[") && cleanValue.contains("]")) {
			return cleanValue.substring(1, cleanValue.indexOf(']'));
		}
		int portSeparator = cleanValue.lastIndexOf(':');
		if (portSeparator > 0 && cleanValue.indexOf(':') == portSeparator
				&& cleanValue.substring(0, portSeparator).contains(".")) {
			return cleanValue.substring(0, portSeparator);
		}
		return cleanValue;
	}
}
