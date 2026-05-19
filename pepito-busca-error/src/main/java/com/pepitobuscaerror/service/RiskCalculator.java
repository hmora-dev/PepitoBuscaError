package com.pepitobuscaerror.service;

import com.pepitobuscaerror.model.Indicator;

import java.util.List;

public interface RiskCalculator {
	int calculateRisk(List<Indicator> indicators);
}
