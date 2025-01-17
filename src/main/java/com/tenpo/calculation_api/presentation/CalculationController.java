package com.tenpo.calculation_api.presentation;

import com.tenpo.calculation_api.application.services.CalculationService;
import com.tenpo.calculation_api.application.services.CallHistoryService;
import com.tenpo.calculation_api.domain.model.CallHistory;
import com.tenpo.calculation_api.presentation.dtos.CalculationRequest;
import com.tenpo.calculation_api.presentation.dtos.CalculationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/calculation")
public class CalculationController {
    public static final Logger LOGGER = LoggerFactory.getLogger(CalculationController.class);
    @Autowired
    private CalculationService calculationService;

    @Autowired
    private PagedResourcesAssembler<CallHistory> pagedResourcesAssembler;

    @PostMapping("")
    public @ResponseBody ResponseEntity<?> calculate(@RequestBody @Valid CalculationRequest calculationRequest) {
        BigDecimal result = calculationService.calculateWithPercentage(calculationRequest.getNum1(), calculationRequest.getNum2());
        return new ResponseEntity<>(new CalculationResponse(result, calculationRequest.getNum1(), calculationRequest.getNum2()), HttpStatus.OK);
    }

    @Autowired
    private CallHistoryService callHistoryService;

    @GetMapping
    public PagedModel<EntityModel<CallHistory>> getCallHistory(@PageableDefault(size = 10) Pageable pageable) {
        Page<CallHistory> callHistoryPage = callHistoryService.getCallHistory(pageable);
        return pagedResourcesAssembler.toModel(callHistoryPage);
    }


}