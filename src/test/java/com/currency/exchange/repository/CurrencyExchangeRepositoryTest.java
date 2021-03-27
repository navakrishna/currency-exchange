package com.currency.exchange.repository;

import com.currency.exchange.repository.dto.CurrencyExchangeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CurrencyExchangeRepositoryTest {

    @Autowired
    private CurrencyExchangeRepository repository;

    @Test
    public void testFindAll() {
        List<CurrencyExchangeDTO> all = repository.findAll();
        assertTrue(!all.isEmpty());
    }

    @Test
    public void testFindByCurrency() {
        Optional<CurrencyExchangeDTO> opt = repository.findByCurrency("USD");
        assertTrue(opt.isPresent());
    }

    @Test
    public void testFindByCurrency_INVALID() {
        Optional<CurrencyExchangeDTO> opt = repository.findByCurrency("USD1");
        assertTrue(opt.isEmpty());
    }

    @Test
    public void getCurrencyVsNoOfRequest() {
        Map<String, Integer> currencyVsNoOfRequest = repository.getCurrencyVsNoOfRequest();
        assertTrue(!currencyVsNoOfRequest.isEmpty());
    }
}
