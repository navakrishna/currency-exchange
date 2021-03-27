package com.currency.exchange.service.bo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CurrencySupport {
    private String currency;
    private Integer numOfTimes;
}
