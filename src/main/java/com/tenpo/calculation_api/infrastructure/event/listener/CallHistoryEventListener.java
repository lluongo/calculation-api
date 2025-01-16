package com.tenpo.calculation_api.infrastructure.event.listener;

import com.tenpo.calculation_api.domain.model.CallHistory;
import com.tenpo.calculation_api.infrastructure.event.CallHistoryEvent;
import com.tenpo.calculation_api.infrastructure.persistence.CallHistoryRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CallHistoryEventListener {

    private final CallHistoryRepository repository;

    public CallHistoryEventListener(CallHistoryRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void handleCallHistoryEvent(CallHistoryEvent event) {
        CallHistory callHistory = new CallHistory(
                event.timestamp(),
                event.endpoint(),
                event.parameters(),
                event.response(),
                event.errorMessage()
        );
        repository.save(callHistory);
    }
}