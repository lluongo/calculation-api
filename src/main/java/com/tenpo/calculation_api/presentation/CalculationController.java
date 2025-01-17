package com.tenpo.calculation_api.presentation;

import com.tenpo.calculation_api.application.services.CalculationService;
import com.tenpo.calculation_api.presentation.dtos.CalculationRequest;
import com.tenpo.calculation_api.presentation.dtos.CalculationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Valid
@RequestMapping("/v1/operation")
public class CalculationController {
    public static final Logger LOGGER = LoggerFactory.getLogger(CalculationController.class);
    @Autowired
    private CalculationService calculationService;

    @PostMapping("")
    public @ResponseBody ResponseEntity<?> calculate(@RequestBody @Valid CalculationRequest calculationRequest) {
        BigDecimal result = calculationService.calculateWithPercentage(calculationRequest.getNum1(), calculationRequest.getNum2());
        return new ResponseEntity<>(new CalculationResponse(result, calculationRequest.getNum1(), calculationRequest.getNum2()), HttpStatus.OK);
    }

}