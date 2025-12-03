package com.ifham.analyzer.service;

import com.ifham.analyzer.dto.AnalysisResultDTO;
import com.ifham.analyzer.entity.ResumeReport;
import com.ifham.analyzer.repository.ResumeReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

@Service
public class ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);  

    private final ResumeReportRepository repo;
    private final Tika tika = new Tika();

    private final List<String> SKILL_KEYWORDS = Arrays.asList(
            "java","spring","spring boot","hibernate","jpa","sql","mysql","oracle",
            "rest","api","microservices","docker","kubernetes","react","javascript",
            "html","css","aws","git","maven","gradle","junit","mockito","postgresql"
    );

    public ResumeService(ResumeReportRepository repo) {
        this.repo = repo;
    }

    public AnalysisResultDTO analyze(MultipartFile file, String jobDescription) throws Exception {
        logger.info("Starting resume analysis for file: {}", file.getOriginalFilename());

        // Efficient InputStream parsing
        String text = parseResumeText(file);
        logger.debug("Resume text extracted successfully (length: {})", text.length());

        AnalysisResultDTO result = new AnalysisResultDTO();

        // 1) Skill Matching
        Map<String, Integer> skillMatches = new HashMap<>();
        String lowerText = text.toLowerCase();
        for (String skill : SKILL_KEYWORDS) {
            int count = countOccurrences(lowerText, skill.toLowerCase());
            if (count > 0) skillMatches.put(skill, count);
        }
        logger.info("Skill matching completed. Total matched skills: {}", skillMatches.size());

        // 2) Top Skills
        List<String> topSkills = new ArrayList<>(skillMatches.keySet());
        topSkills.sort((a, b) -> Integer.compare(skillMatches.get(b), skillMatches.get(a)));
        if (topSkills.size() > 7) topSkills = topSkills.subList(0, 7);
        logger.debug("Top skills identified: {}", topSkills);

        // 3) Experience Extraction
        String expSummary = extractExperienceSummary(text);
        logger.debug("Extracted experience summary: {}", expSummary);

        // 4) Education Extraction
        String educationSummary = extractEducation(text);
        logger.debug("Extracted education summary: {}", educationSummary);

        // 5) Compute Scores
        double skillScore = computeSkillScore(jobDescription, lowerText);
        double experienceScore = computeExperienceScore(expSummary);
        double eduScore = educationSummary.isEmpty() ? 0 : 10;
        double finalScore = Math.min(100, skillScore * 0.6 + experienceScore * 0.3 + eduScore * 0.1);

        logger.info("Scores computed -> Skill: {}, Exp: {}, Edu: {}, Final: {}",
                skillScore, experienceScore, eduScore, finalScore);

        // 6) Suggestions
        String suggestions = buildSuggestions(skillMatches, jobDescription, expSummary);

        // Fill DTO
        result.setMatchScore(Math.round(finalScore * 100.0) / 100.0);
        result.setSkillMatches(skillMatches);
        result.setTopSkills(topSkills);
        result.setExperienceSummary(expSummary);
        result.setEducationSummary(educationSummary);
        result.setSuggestions(suggestions);

        // Persist report
        ResumeReport report = new ResumeReport();
        report.setFileName(file.getOriginalFilename());
        report.setCandidateName(extractName(text));
        report.setMatchScore(result.getMatchScore());

        String safeText = text.length() > 5000 ? text.substring(0, 5000) + "..." : text;
        report.setExtractedText(safeText);

        String summaryJson = "skills:" + skillMatches + ";top:" + topSkills;
        report.setSummaryJson(summaryJson);
        report.setCreatedAt(LocalDateTime.now());

        repo.save(report);
        logger.info("Resume report saved successfully for: {}", report.getCandidateName());

        return result;
    }

    private String parseResumeText(MultipartFile file) throws IOException, TikaException {
        logger.debug("Parsing resume text using Apache Tika...");
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        } catch (IOException e) {
            logger.error("Error while parsing resume: {}", e.getMessage());
            throw e;
        }
    }

    private int countOccurrences(String text, String term) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(term) + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

    private String extractExperienceSummary(String text) {
        Pattern p = Pattern.compile("(\\d{1,2})\\+?\\s*(years|yrs|year)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        int max = 0;
        while (m.find()) {
            try {
                int v = Integer.parseInt(m.group(1));
                if (v > max) max = v;
            } catch (Exception ignored) {}
        }
        if (max == 0) return "Experience not clearly mentioned";
        return max + " years (detected)";
    }

    private String extractEducation(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("phd") || lower.contains("doctor")) return "PhD";
        if (lower.contains("master") || lower.contains("m.tech") || lower.contains("m.sc")) return "Master's";
        if (lower.contains("bachelor") || lower.contains("b.tech") || lower.contains("bsc") || lower.contains("be")) return "Bachelor's";
        return "";
    }

    private double computeSkillScore(String jobDesc, String resumeTextLower) {
        if (jobDesc == null) jobDesc = "";
        jobDesc = jobDesc.toLowerCase();
        int totalKeywords = 0;
        int matched = 0;
        for (String s : SKILL_KEYWORDS) {
            if (jobDesc.contains(s)) {
                totalKeywords++;
                if (resumeTextLower.contains(s)) matched++;
            }
        }
        return totalKeywords == 0 ? 0 : ((double) matched / totalKeywords) * 100;
    }

    private double computeExperienceScore(String expSummary) {
        if (expSummary == null || expSummary.contains("not clearly")) return 30;
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(expSummary);
        if (m.find()) {
            int years = Integer.parseInt(m.group(1));
            return Math.min(70, 30 + years * 5);
        }
        return 30;
    }

    private String buildSuggestions(Map<String,Integer> skills, String jobDesc, String expSummary) {
        StringBuilder sb = new StringBuilder();
        if (skills.isEmpty()) sb.append("Add a technical skills section: Java, Spring Boot, SQL, etc. ");
        else sb.append("Highlight top skills: ").append(String.join(", ", skills.keySet())).append(". ");
        if (expSummary.contains("not clearly")) sb.append("Mention total years of experience with dates. ");
        sb.append("Tailor your resume to match job description keywords. ");
        return sb.toString();
    }

    private String extractName(String text) {
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < Math.min(8, lines.length); i++) {
            String line = lines[i].trim();
            if (line.length() > 3 && line.length() < 60 && line.matches(".*[A-Za-z].*")) {
                return line;
            }
        }
        return "";
    }
}
