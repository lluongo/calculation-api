package com.tenpo.calculation_api.presentation.dtos;

import lombok.Data;

@Data
public class GetHistoryRequest {
    private int page;
    private int size;
}
