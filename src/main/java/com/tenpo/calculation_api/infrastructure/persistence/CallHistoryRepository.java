package com.tenpo.calculation_api.infrastructure.persistence;

import com.tenpo.calculation_api.domain.model.CallHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
}

