package com.currency.exchange.service.impl;

import com.currency.exchange.exception.CurrencyNotFoundException;
import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.repository.CurrencyExchangeRepository;
import com.currency.exchange.repository.dto.CurrencyExchangeDTO;
import com.currency.exchange.service.CurrencyExchangeService;
import com.currency.exchange.service.bo.ApplicationProperties;
import com.currency.exchange.service.bo.CurrencyConversion;
import com.currency.exchange.service.bo.CurrencyExchange;
import com.currency.exchange.service.bo.CurrencySupport;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private CurrencyExchangeRepository repository;

    private ApplicationProperties properties;

    public CurrencyExchangeServiceImpl(CurrencyExchangeRepository repository, ApplicationProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Override
    public CurrencyExchange getExchangeRateToEuro(String from) throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchangeDTO fromCurrencyExchangeDTO = getCurrencyExchangeDTO(from, "from");
        return getCurrencyExchange(fromCurrencyExchangeDTO.getCurrency(), "EUR", fromCurrencyExchangeDTO.getCountry(), "Europe", BigDecimal.ONE.divide(fromCurrencyExchangeDTO.getExchangeRate(), 3, RoundingMode.HALF_UP));
    }

    @Override
    public CurrencyExchange getExchangeRateFromCurrencyPairs(String from, String to) throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchange fromCurrencyExchange = getExchangeRateToEuro(from);
        CurrencyExchangeDTO toCurrencyExchangeDTO = getCurrencyExchangeDTO(to, "to");
        BigDecimal exchangeRate = calculateExchangeRate(fromCurrencyExchange.getExchangeRate(), toCurrencyExchangeDTO.getExchangeRate());
        return getCurrencyExchange(from, to, fromCurrencyExchange.getFromCountry(), toCurrencyExchangeDTO.getCountry(), exchangeRate);
    }

    @Override
    public List<CurrencySupport> getSupportedCurrenciesAndNoOfTimesRequested() {
        return repository.getCurrencyVsNoOfRequest().entrySet()
                .stream()
                .map(each -> CurrencySupport.builder()
                        .currency(each.getKey())
                        .numOfTimes(each.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyConversion getCurrencyConversion(String from, String to, BigDecimal quantity) throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchange exchange = getExchangeRateFromCurrencyPairs(from, to);
        return CurrencyConversion.builder()
                .exchangeRate(exchange.getExchangeRate())
                .from(exchange.getFromCurrency())
                .to(exchange.getToCurrency())
                .quantity(quantity).calculateAmount(exchange.getExchangeRate().multiply(quantity)).build();
    }

    @Override
    public Map<String, String> getCurrencyPairLink(String currency) throws CurrencyNotFoundException {
        String url = properties.getUrl();
        String link = properties.getCurrencyLinks().get(currency);
        if(link == null) {
            throw new CurrencyNotFoundException("No currency chart url found for "+currency);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("currencyPair", url.replace("currencyLink", link));
        return map;
    }

    private CurrencyExchange getCurrencyExchange(String fromCurrency, String toCurrency, String fromCountry, String toCountry, BigDecimal exchangeRate) {
        return CurrencyExchange.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .toCountry(toCountry)
                .fromCountry(fromCountry)
                .exchangeRate(exchangeRate).build();
    }

    private CurrencyExchangeDTO getCurrencyExchangeDTO(String currency, String attribute) throws InvalidCurrencyException, CurrencyNotFoundException {
        validateInputs(currency, "The given %s can't be null or empty", attribute);
        Optional<CurrencyExchangeDTO> fromCurrency = repository.findByCurrency(currency);
        validateDBResponse(fromCurrency, "No currency found for " + currency);
        return fromCurrency.get();
    }

    private BigDecimal calculateExchangeRate(BigDecimal fromCurrencyRate, BigDecimal toCurrencyRate) {
        return toCurrencyRate.multiply(fromCurrencyRate).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateInputs(String input, String message, String attribute) throws InvalidCurrencyException {
        if (input == null || input.isBlank()) {
            throw new InvalidCurrencyException(String.format(message, attribute));
        }
    }

    private void validateDBResponse(Optional<CurrencyExchangeDTO> dto, String message) throws CurrencyNotFoundException {
        if (dto.isEmpty()) {
            throw new CurrencyNotFoundException(message);
        }
    }
}
