package com.tenpo.calculation_api.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public CallHistory( LocalDateTime timestamp, String endpoint, String parameters, String response, String error) {
        this.timestamp = timestamp;
        this.endpoint = endpoint;
        this.parameters = parameters;
        this.response = response;
        this.error = error;
    }
}
