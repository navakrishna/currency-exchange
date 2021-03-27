package com.currency.exchange.advice;

import lombok.Data;

@Data
public class CurrencyExchangeErrorResponse {

    private String title;
    private String type;
    private String detail;
    private String instance;
    private Integer status;
    public CurrencyExchangeErrorResponse() {

    }

    public CurrencyExchangeErrorResponse(Integer status, String type, String title, String detail, String instance) {
        super();
        this.status = status;
        this.type = type;
        this.title = title;
        this.instance = instance;
        this.detail = detail;
    }
}
