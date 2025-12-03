package com.ifham.analyzer.controller;

import com.ifham.analyzer.dto.AnalysisResultDTO;
import com.ifham.analyzer.service.ResumeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resume")
public class ResumeController {

    private final ResumeService service;
    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @PostMapping(value = "/analyze", consumes = {"multipart/form-data"})
    public ResponseEntity<?> analyze(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "jobDescription", required = false) String jobDescription
    ) {
    	logger.info("Recieved resume analysis request .");
        try {
        	//1) check if file is null or empty.
        	if(file==null || file.isEmpty()) {
        		logger.warn("File is null or empty ");
        		return ResponseEntity.badRequest().body("Resume file is required and cannot be empty.");
        	}
        	
        	//2) Validate file type(only .pdf, .doc, .docx)
        	String fileName = file.getOriginalFilename();
        	logger.debug("Checking file type : {}",fileName);
        	
        	if(fileName == null || !(fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx"))) {
                logger.warn("Invalid file type: {}", fileName);
        		return ResponseEntity.badRequest().body("Invalid file type . Only PDF , DOC , or DOCX files are allowed.");
        	}
        	
        	//3) File size (max 5MB)
        	long maxFileSize = 5 * 1024 * 1024;
        	if(file.getSize() > maxFileSize) {
                logger.warn("File too large: {} bytes (limit 5 MB)", file.getSize());
        		return ResponseEntity.badRequest().body("File size exceeds 5 MB limit.");
        	}
            logger.info("File validation passed: {}", fileName);

            AnalysisResultDTO result = service.analyze(file, jobDescription);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error while analyzing resume: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error parsing resume: " + e.getMessage());
        }
    }
}
