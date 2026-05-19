package com.pepitobuscaerror.dto;

import java.util.ArrayList;
import java.util.List;

public class AnalysisForm {

	private List<String> selectedIndicators = new ArrayList<>();

	public List<String> getSelectedIndicators() {
		return selectedIndicators;
	}

	public void setSelectedIndicators(List<String> selectedIndicators) {
		this.selectedIndicators = selectedIndicators == null ? new ArrayList<>() : selectedIndicators;
	}
}
