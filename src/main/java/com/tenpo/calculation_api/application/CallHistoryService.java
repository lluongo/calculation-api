package com.tenpo.calculation_api.application;

import com.tenpo.calculation_api.domain.model.CallHistory;
import com.tenpo.calculation_api.infrastructure.persistence.CallHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CallHistoryService {
    @Autowired
    private CallHistoryRepository callHistoryRepository;

    public Page<CallHistory> getCallHistory(Pageable pageable) {
        return callHistoryRepository.findAll(pageable);
    }
}
