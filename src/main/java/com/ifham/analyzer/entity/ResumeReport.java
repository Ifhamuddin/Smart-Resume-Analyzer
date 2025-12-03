package com.ifham.analyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_reports")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String candidateName; // optional, if parsable
    private double matchScore; // 0-100
    @Column(columnDefinition = "TEXT")
    private String extractedText; // big text
    @Column(columnDefinition = "TEXT")
    private String summaryJson; // JSON string with detailed results
    private LocalDateTime createdAt;

  }
