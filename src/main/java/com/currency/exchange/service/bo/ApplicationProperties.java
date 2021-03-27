package com.currency.exchange.service.bo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "exchange")
public class ApplicationProperties {
    private String url;
    private Map<String, String> currencyLinks;
}
