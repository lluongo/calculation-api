package com.tenpo.calculation_api.infrastructure.event;

import java.time.LocalDateTime;

public record CallHistoryEvent(
        String endpoint,
        String parameters,
        String response,
        String errorMessage,
        LocalDateTime timestamp
) {
}
