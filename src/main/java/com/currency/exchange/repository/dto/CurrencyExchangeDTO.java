package com.currency.exchange.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyExchangeDTO {
    private String currency;
    private BigDecimal exchangeRate;
    private String country;
    private String description;
}
