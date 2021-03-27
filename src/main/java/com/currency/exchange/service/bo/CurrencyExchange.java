package com.currency.exchange.service.bo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CurrencyExchange {
    private String fromCurrency;
    private String toCurrency;
    private String fromCountry;
    private String toCountry;
    private BigDecimal exchangeRate;
}
