package com.currency.exchange.repository.impl;

import com.currency.exchange.repository.dto.CurrencyExchangeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.currency.exchange.repository.CurrencyExchangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class CurrencyExchangeRepositoryImpl implements CurrencyExchangeRepository {

    private Logger logger = LoggerFactory.getLogger(CurrencyExchangeRepositoryImpl.class);

    private static final  Map<String, Integer> currencyVsNoOfRequest = new HashMap<>();


    @Autowired
    private ObjectMapper mapper;

    private List<CurrencyExchangeDTO> currencyExchangeDTOS;


    @Override
    public List<CurrencyExchangeDTO> findAll() {
        return getData();
    }

    @Override
    public Optional<CurrencyExchangeDTO> findByCurrency(String currency) {
        return getData().stream().filter(each -> {
            verifyAndUpdateNoOfRequest(each.getCurrency());
            return currency != null && each.getCurrency().equals(currency);
        }).findAny();
    }

    @Override
    public Map<String, Integer> getCurrencyVsNoOfRequest() {
        if(currencyVsNoOfRequest.isEmpty()) {
            List<CurrencyExchangeDTO> data = getData();
            data.stream().forEach(each -> currencyVsNoOfRequest.put(each.getCurrency(), 0));
        }
        return new HashMap<>(currencyVsNoOfRequest);
    }

    private List<CurrencyExchangeDTO> getData() {
        try {
            return Arrays.asList(mapper.readValue(new ClassPathResource("json/currencyexchange.json").getInputStream(), CurrencyExchangeDTO[].class));
        } catch (IOException e) {
            logger.error("An error occurred while reading the currency json file", e);
        }
        return new ArrayList<>();
    }

    private void verifyAndUpdateNoOfRequest(String currency) {
        if (currencyVsNoOfRequest.containsKey(currency)) {
            Integer counter = currencyVsNoOfRequest.get(currency);
            currencyVsNoOfRequest.put(currency, counter+1);
        }
    }
}