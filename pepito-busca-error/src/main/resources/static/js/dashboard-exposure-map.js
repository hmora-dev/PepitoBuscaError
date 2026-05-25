(function () {
	"use strict";

	const container = document.getElementById("dashboard-exposure-map");
	const canvas = document.getElementById("dashboard-exposure-canvas");
	if (!container || !canvas) {
		return;
	}

	const context = canvas.getContext("2d");
	if (!context) {
		return;
	}

	const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
	const data = {
		companies: readNumber("companies"),
		analyses: readNumber("analyses"),
		osint: readNumber("osint"),
		averageRisk: readNumber("averageRisk"),
		critical: readNumber("critical"),
		high: readNumber("high"),
		recommendations: readNumber("recommendations")
	};
	const palette = {
		blue: "#3b82f6",
		cyan: "#2563eb",
		green: "#22c55e",
		amber: "#eab308",
		red: "#ef4444",
		white: "#ffffff",
		muted: "#888888",
		panel: "rgba(0, 0, 0, 0.82)"
	};
	let width = 0;
	let height = 0;
	let devicePixelRatioValue = 1;
	let animationFrame = null;
	let pointer = {x: 0.62, y: 0.48, active: false};

	const nodes = [
		{label: "Companies", value: data.companies, x: 0.20, y: 0.28, color: palette.blue, orbit: 0.00},
		{label: "Analyses", value: data.analyses, x: 0.78, y: 0.25, color: palette.cyan, orbit: 1.40},
		{label: "OSINT", value: data.osint, x: 0.72, y: 0.74, color: palette.green, orbit: 2.70},
		{label: "Critical", value: data.critical, x: 0.28, y: 0.75, color: palette.red, orbit: 4.00},
		{label: "High", value: data.high, x: 0.48, y: 0.18, color: palette.amber, orbit: 5.15}
	];

	function readNumber(name) {
		const value = Number(container.dataset[name] || 0);
		return Number.isFinite(value) ? value : 0;
	}

	function resize() {
		const rect = container.getBoundingClientRect();
		width = Math.max(320, rect.width);
		height = Math.max(260, rect.height);
		devicePixelRatioValue = Math.min(window.devicePixelRatio || 1, 2);
		canvas.width = Math.floor(width * devicePixelRatioValue);
		canvas.height = Math.floor(height * devicePixelRatioValue);
		canvas.style.width = width + "px";
		canvas.style.height = height + "px";
		context.setTransform(devicePixelRatioValue, 0, 0, devicePixelRatioValue, 0, 0);
		draw(performance.now());
	}

	function render(timestamp) {
		draw(timestamp);
		animationFrame = window.requestAnimationFrame(render);
	}

	function draw(timestamp) {
		const time = prefersReducedMotion ? 0 : timestamp / 1000;
		context.clearRect(0, 0, width, height);
		drawBackground(time);
		drawLinks(time);
		drawCentralCore(time);
		drawNodes(time);
		drawScanSweep(time);
	}

	function drawBackground(time) {
		const gradient = context.createLinearGradient(0, 0, width, height);
		gradient.addColorStop(0, "#000000");
		gradient.addColorStop(0.52, "#0d0d0d");
		gradient.addColorStop(1, "#000000");
		context.fillStyle = gradient;
		context.fillRect(0, 0, width, height);

		context.save();
		context.globalAlpha = 0.25;
		context.strokeStyle = "rgba(37, 99, 235, 0.18)";
		context.lineWidth = 1;
		const gridSize = 34;
		const offset = (time * 10) % gridSize;
		for (let x = -gridSize + offset; x < width + gridSize; x += gridSize) {
			context.beginPath();
			context.moveTo(x, 0);
			context.lineTo(x, height);
			context.stroke();
		}
		for (let y = -gridSize + offset; y < height + gridSize; y += gridSize) {
			context.beginPath();
			context.moveTo(0, y);
			context.lineTo(width, y);
			context.stroke();
		}
		context.restore();

		for (let index = 0; index < 42; index += 1) {
			const x = ((index * 83) % width) + Math.sin(time * 0.45 + index) * 5;
			const y = ((index * 47) % height) + Math.cos(time * 0.38 + index) * 5;
			const alpha = 0.12 + ((index % 5) * 0.025);
			context.fillStyle = "rgba(191, 219, 254, " + alpha + ")";
			context.beginPath();
			context.arc(x, y, 1.15, 0, Math.PI * 2);
			context.fill();
		}
	}

	function drawLinks(time) {
		const center = centerPoint();
		nodes.forEach(function (node, index) {
			const point = nodePoint(node, time);
			const progress = (Math.sin(time * 1.6 + index) + 1) / 2;
			const gradient = context.createLinearGradient(center.x, center.y, point.x, point.y);
			gradient.addColorStop(0, "rgba(96, 165, 250, 0.70)");
			gradient.addColorStop(progress, node.color);
			gradient.addColorStop(1, "rgba(148, 163, 184, 0.24)");

			context.save();
			context.strokeStyle = gradient;
			context.lineWidth = 1.4 + riskIntensity() * 1.4;
			context.shadowBlur = 16;
			context.shadowColor = node.color;
			context.beginPath();
			context.moveTo(center.x, center.y);
			const curveX = (center.x + point.x) / 2 + Math.sin(time + index) * 18;
			const curveY = (center.y + point.y) / 2 + Math.cos(time * 0.9 + index) * 16;
			context.quadraticCurveTo(curveX, curveY, point.x, point.y);
			context.stroke();
			context.restore();
		});
	}

	function drawCentralCore(time) {
		const center = centerPoint();
		const radius = Math.min(width, height) * 0.16;
		const ringPulse = Math.sin(time * 2.4) * 4;
		const criticalGlow = Math.min(1, (data.critical + data.high) / 12);

		context.save();
		context.shadowBlur = 42 + criticalGlow * 18;
		context.shadowColor = criticalGlow > 0.35 ? "rgba(239, 68, 68, 0.55)" : "rgba(37, 99, 235, 0.55)";
		context.fillStyle = "rgba(0, 0, 0, 0.92)";
		context.beginPath();
		context.arc(center.x, center.y, radius, 0, Math.PI * 2);
		context.fill();
		context.restore();

		drawArc(center.x, center.y, radius + 11 + ringPulse, -Math.PI / 2,
				-Math.PI / 2 + Math.PI * 2 * Math.max(0.06, data.averageRisk / 100), riskColor(), 6);
		drawArc(center.x, center.y, radius + 24, time, time + Math.PI * 1.25, "rgba(37, 99, 235, 0.65)", 2);
		drawArc(center.x, center.y, radius + 34, -time * 0.72, -time * 0.72 + Math.PI * 1.55,
				"rgba(59, 130, 246, 0.35)", 1.5);

		context.fillStyle = palette.white;
		context.font = "900 " + Math.max(34, radius * 0.72) + "px Inter, system-ui, sans-serif";
		context.textAlign = "center";
		context.textBaseline = "middle";
		context.fillText(String(Math.round(data.averageRisk)), center.x, center.y - 5);

		context.fillStyle = palette.muted;
		context.font = "800 11px Inter, system-ui, sans-serif";
		context.fillText("AVG RISK", center.x, center.y + radius * 0.42);
	}

	function drawNodes(time) {
		nodes.forEach(function (node) {
			const point = nodePoint(node, time);
			const radius = nodeRadius(node.value);
			const isPriority = node.label === "Critical" || node.label === "High";

			context.save();
			context.shadowBlur = isPriority ? 28 : 20;
			context.shadowColor = node.color;
			context.fillStyle = "rgba(15, 23, 42, 0.84)";
			context.beginPath();
			context.arc(point.x, point.y, radius + 7, 0, Math.PI * 2);
			context.fill();

			context.fillStyle = node.color;
			context.beginPath();
			context.arc(point.x, point.y, radius, 0, Math.PI * 2);
			context.fill();

			context.fillStyle = "rgba(255, 255, 255, 0.92)";
			context.beginPath();
			context.arc(point.x - radius * 0.28, point.y - radius * 0.32, Math.max(2, radius * 0.28), 0, Math.PI * 2);
			context.fill();
			context.restore();

			drawNodeLabel(node, point, radius);
		});
	}

	function drawNodeLabel(node, point, radius) {
		context.font = "900 13px Inter, system-ui, sans-serif";
		const labelWidth = Math.max(82, context.measureText(node.label).width + 36);
		const labelX = clamp(point.x - labelWidth / 2, 12, width - labelWidth - 12);
		const labelY = clamp(point.y + radius + 12, 12, height - 48);

		context.save();
		context.fillStyle = palette.panel;
		roundedRect(labelX, labelY, labelWidth, 38, 10);
		context.fill();
		context.strokeStyle = "rgba(148, 163, 184, 0.20)";
		context.stroke();

		context.fillStyle = palette.white;
		context.font = "900 13px Inter, system-ui, sans-serif";
		context.textAlign = "left";
		context.textBaseline = "top";
		context.fillText(String(node.value), labelX + 11, labelY + 6);

		context.fillStyle = palette.muted;
		context.font = "800 10px Inter, system-ui, sans-serif";
		context.fillText(node.label.toUpperCase(), labelX + 11, labelY + 22);
		context.restore();
	}

	function drawScanSweep(time) {
		const center = centerPoint();
		const radius = Math.min(width, height) * 0.47;
		const angle = time * 0.9;

		context.save();
		context.translate(center.x, center.y);
		context.rotate(angle);
		const gradient = context.createRadialGradient(0, 0, 0, 0, 0, radius);
		gradient.addColorStop(0, "rgba(34, 211, 238, 0)");
		gradient.addColorStop(0.65, "rgba(34, 211, 238, 0.10)");
		gradient.addColorStop(1, "rgba(34, 211, 238, 0)");
		context.fillStyle = gradient;
		context.beginPath();
		context.moveTo(0, 0);
		context.arc(0, 0, radius, -0.11, 0.11);
		context.closePath();
		context.fill();
		context.restore();
	}

	function centerPoint() {
		const pointerPull = pointer.active ? 12 : 0;
		return {
			x: width * 0.52 + (pointer.x - 0.5) * pointerPull,
			y: height * 0.50 + (pointer.y - 0.5) * pointerPull
		};
	}

	function nodePoint(node, time) {
		const drift = prefersReducedMotion ? 0 : 10;
		return {
			x: width * node.x + Math.cos(time * 0.55 + node.orbit) * drift,
			y: height * node.y + Math.sin(time * 0.68 + node.orbit) * drift
		};
	}

	function nodeRadius(value) {
		return 8 + Math.min(18, Math.sqrt(Math.max(0, value)) * 4);
	}

	function riskIntensity() {
		return Math.min(1, Math.max(0, data.averageRisk / 100));
	}

	function riskColor() {
		if (data.averageRisk >= 75 || data.critical > 0) {
			return palette.red;
		}
		if (data.averageRisk >= 45 || data.high > 0) {
			return palette.amber;
		}
		if (data.averageRisk >= 20) {
			return palette.cyan;
		}
		return palette.green;
	}

	function drawArc(x, y, radius, start, end, color, lineWidth) {
		context.save();
		context.strokeStyle = color;
		context.lineWidth = lineWidth;
		context.lineCap = "round";
		context.beginPath();
		context.arc(x, y, radius, start, end);
		context.stroke();
		context.restore();
	}

	function roundedRect(x, y, w, h, r) {
		context.beginPath();
		context.moveTo(x + r, y);
		context.arcTo(x + w, y, x + w, y + h, r);
		context.arcTo(x + w, y + h, x, y + h, r);
		context.arcTo(x, y + h, x, y, r);
		context.arcTo(x, y, x + w, y, r);
		context.closePath();
	}

	function clamp(value, min, max) {
		return Math.min(Math.max(value, min), max);
	}

	container.addEventListener("pointermove", function (event) {
		const rect = container.getBoundingClientRect();
		pointer = {
			x: clamp((event.clientX - rect.left) / rect.width, 0, 1),
			y: clamp((event.clientY - rect.top) / rect.height, 0, 1),
			active: true
		};
	});

	container.addEventListener("pointerleave", function () {
		pointer.active = false;
	});

	window.addEventListener("resize", resize);
	if ("ResizeObserver" in window) {
		new ResizeObserver(resize).observe(container);
	}

	resize();
	if (!prefersReducedMotion) {
		window.cancelAnimationFrame(animationFrame);
		animationFrame = window.requestAnimationFrame(render);
	}
}());
