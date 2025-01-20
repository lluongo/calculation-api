package com.tenpo.calculation_api.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
public class CallHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private String error;

    public CallHistory() {
    }

    @PrePersist
    @PreUpdate
    private void sanitizeFields() {
        if (response != null) {
            response = response.replace("\0", "");
        }
    }

    public CallHistory(LocalDateTime timestamp, String endpoint, String parameters, String response, String error) {
        this.timestamp = timestamp;
        this.endpoint = endpoint;
        this.parameters = parameters;
        this.response = response;
        this.error = error;
    }
}
