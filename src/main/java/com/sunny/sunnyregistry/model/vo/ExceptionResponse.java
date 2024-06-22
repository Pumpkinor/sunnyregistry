package com.sunny.sunnyregistry.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {
    private String errCode;
    private String errMessage;
}
