package com.winwin.authapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processing_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @Column(columnDefinition = "TEXT")
    private String inputText;

    @Column(columnDefinition = "TEXT")
    private String outputText;

    private LocalDateTime createdAt;
}