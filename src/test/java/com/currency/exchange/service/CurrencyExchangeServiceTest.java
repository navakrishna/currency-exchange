package com.currency.exchange.service;


import com.currency.exchange.exception.CurrencyNotFoundException;
import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.repository.CurrencyExchangeRepository;
import com.currency.exchange.repository.dto.CurrencyExchangeDTO;
import com.currency.exchange.service.bo.CurrencyConversion;
import com.currency.exchange.service.bo.CurrencyExchange;
import com.currency.exchange.service.bo.CurrencySupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CurrencyExchangeServiceTest {

    @Autowired
    private CurrencyExchangeService service;

    @MockBean
    private CurrencyExchangeRepository repository;

    @Test
    public void testGetExchangeRateToEuro() throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchangeDTO dto = new CurrencyExchangeDTO("USD",new BigDecimal(1.18), "United States", "US Dollars");
        when(repository.findByCurrency("USD")).thenReturn(Optional.of(dto));

        CurrencyExchange exchange = service.getExchangeRateToEuro("USD");
        assertEquals(exchange.getFromCurrency(), dto.getCurrency());
        assertEquals(exchange.getExchangeRate(), new BigDecimal("0.847"));
    }

    @Test
    public void testGetExchangeRateToEuroWithInvalidInputs() throws InvalidCurrencyException, CurrencyNotFoundException {

        InvalidCurrencyException InvalidCurrencyException = assertThrows(InvalidCurrencyException.class, () -> {
            service.getExchangeRateToEuro(" ");
        });
        assertTrue("The given from can't be null or empty".equals(InvalidCurrencyException.getMessage()));
    }

    @Test
    public void testGetExchangeRateToEUROForCurrencyNotFound() throws InvalidCurrencyException, CurrencyNotFoundException {

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            service.getExchangeRateToEuro("USD11");
        });
        assertTrue("No currency found for USD11".equals(exception.getMessage()));
    }

    @Test
    public void testGetExchangeRateForCurrencyPairs() throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchangeDTO fromDTO = new CurrencyExchangeDTO("USD",new BigDecimal(1.186), "United States", "US Dollars");
        CurrencyExchangeDTO toDTO = new CurrencyExchangeDTO("HUF",new BigDecimal(366.83),  "Hungary", "Hungary forint");
        when(repository.findByCurrency("USD")).thenReturn(Optional.of(fromDTO));
        when(repository.findByCurrency("HUF")).thenReturn(Optional.of(toDTO));
        CurrencyExchange exchange = service.getExchangeRateFromCurrencyPairs("USD", "HUF");
        assertEquals(exchange.getFromCurrency(), fromDTO.getCurrency());
        assertEquals(exchange.getToCurrency(), toDTO.getCurrency());
        assertEquals(exchange.getExchangeRate().setScale(2, RoundingMode.HALF_UP), new BigDecimal(309.24).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void testGetExchangeRateForCurrencyPairs_CurrencyNotFound() throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchangeDTO toDTO = new CurrencyExchangeDTO("HUF",new BigDecimal(366.83), "Hungary", "Hungary forint");
        when(repository.findByCurrency("HUF")).thenReturn(Optional.of(toDTO));
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            service.getExchangeRateFromCurrencyPairs("HUF", "USD11");
        });
        assertTrue("No currency found for USD11".equals(exception.getMessage()));
    }

    @Test
    public void testGetCurrencyConversions() throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyExchangeDTO fromDTO = new CurrencyExchangeDTO("USD",new BigDecimal(1.186),"United States", "US Dollars");
        CurrencyExchangeDTO toDTO = new CurrencyExchangeDTO("HUF",new BigDecimal(366.83),"Hungary", "Hungary forint");
        when(repository.findByCurrency("USD")).thenReturn(Optional.of(fromDTO));
        when(repository.findByCurrency("HUF")).thenReturn(Optional.of(toDTO));
        CurrencyConversion conversion = service.getCurrencyConversion("USD", "HUF", new BigDecimal(15));
        assertEquals(conversion.getFrom(), fromDTO.getCurrency());
        assertEquals(conversion.getTo(), toDTO.getCurrency());
        assertEquals(conversion.getExchangeRate().setScale(2, RoundingMode.HALF_UP), new BigDecimal(309.24).setScale(2, RoundingMode.HALF_UP));
        assertEquals(conversion.getCalculateAmount().setScale(2, RoundingMode.HALF_UP), new BigDecimal(4638.60).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void testGetSupportedCurrencies() throws InvalidCurrencyException, CurrencyNotFoundException {
        Map<String, Integer> currenciesVsNoOfRequests = new HashMap<>();
        currenciesVsNoOfRequests.put("USD", 2);
        currenciesVsNoOfRequests.put("HUF", 1);
        when(repository.getCurrencyVsNoOfRequest()).thenReturn(currenciesVsNoOfRequests);
        List<CurrencySupport> currencies = service.getSupportedCurrenciesAndNoOfTimesRequested();
        assertThat(currencies).isNotEmpty();
        assertThat(currencies).extracting("currency").isEqualTo(Stream.of("USD","HUF").collect(Collectors.toList()));
        assertThat(currencies).extracting("numOfTimes").isEqualTo(Stream.of(2,1).collect(Collectors.toList()));
    }

    @Test
    public void getCurrencyPairUrl() throws CurrencyNotFoundException {
        Map<String, String> currencyPairLink = service.getCurrencyPairLink("EUR-USD");
        assertTrue("https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/eurofxref-graph-usd.en.html".equals(currencyPairLink.get("currencyPair")));
    }

    @Test
    public void getCurrencyPairUrl_NOT_FOUND() throws CurrencyNotFoundException {
        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            service.getCurrencyPairLink("EUR-USD1");
        });
        assertNotEquals("No currency chart url found for USD11", exception.getMessage());
    }

}
