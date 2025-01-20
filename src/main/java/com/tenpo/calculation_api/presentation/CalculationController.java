package com.tenpo.calculation_api.presentation;

import com.tenpo.calculation_api.application.CalculationService;
import com.tenpo.calculation_api.application.CallHistoryService;
import com.tenpo.calculation_api.domain.model.CallHistory;
import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import com.tenpo.calculation_api.presentation.dtos.CalculationRequest;
import com.tenpo.calculation_api.presentation.dtos.CalculationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

    @Operation(summary = "Post CalculationRequest (num1 y num2)", description = "El EP suma 2 valores, calcula un porcentage obtenido de api externa y finalmente responde la suma total de los 2 parametros mas el calculo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully", content = @Content(schema = @Schema(implementation = CalculationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found") ,
            @ApiResponse(responseCode = "429", description = "Too many requests - please try again later.", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "400", description = "BadRequest", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "503", description = "No se pudo obtener el porcentaje desde el servicio externo o no hay valor en caché", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    }
    )
    @PostMapping("")
    public @ResponseBody ResponseEntity<?> calculate(@RequestBody @Valid CalculationRequest calculationRequest) {
        BigDecimal result = calculationService.calculateWithPercentage(calculationRequest.getNum1(), calculationRequest.getNum2());
        return new ResponseEntity<>(new CalculationResponse(result, calculationRequest.getNum1(), calculationRequest.getNum2()), HttpStatus.OK);
    }

    @Autowired
    private CallHistoryService callHistoryService;

    @GetMapping
    public PagedModel<CallHistory> callHistory(@PageableDefault(sort = "timestamp", size = 10) Pageable pageable) {

        Page<CallHistory> callHistories = callHistoryService.getCallHistory(pageable);

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                callHistories.getSize(),
                callHistories.getNumber(),
                callHistories.getTotalElements(),
                callHistories.getTotalPages()
        );

        PagedModel<CallHistory> pagedModel = PagedModel.of(callHistories.getContent(), metadata);

        pagedModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CalculationController.class)
                .callHistory(pageable)).withSelfRel());

        return pagedModel;
    }


}