package com.tenpo.calculation_api.presentation;

import com.tenpo.calculation_api.application.services.CalculationService;
import com.tenpo.calculation_api.presentation.dtos.CalculationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/operation")
public class CalculationController {

    @Autowired
    private CalculationService calculationService;

    @PostMapping("")
    public @ResponseBody ResponseEntity<?> calculate(@RequestBody CalculationRequest calculationRequest) {
        return new ResponseEntity<>(calculationService.calculateWithPercentage(calculationRequest.getNum1(), calculationRequest.getNum2()), HttpStatus.OK);
    }
}