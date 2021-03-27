package com.currency.exchange.criteria;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyExchangeFilterCriteria {
    private String currency;
    private BigDecimal exchangeRate;
}

