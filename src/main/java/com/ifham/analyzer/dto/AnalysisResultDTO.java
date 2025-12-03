package com.ifham.analyzer.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;
@Data
public class AnalysisResultDTO {
    private double matchScore;
    private Map<String, Integer> skillMatches;
    private List<String> topSkills;
    private String experienceSummary;
    private String educationSummary;
    private String suggestions;
}
		