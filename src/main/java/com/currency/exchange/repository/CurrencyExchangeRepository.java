package com.currency.exchange.repository;

import com.currency.exchange.repository.dto.CurrencyExchangeDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CurrencyExchangeRepository {

    List<CurrencyExchangeDTO> findAll();

    Optional<CurrencyExchangeDTO> findByCurrency(String currency);

    Map<String, Integer> getCurrencyVsNoOfRequest();

}
