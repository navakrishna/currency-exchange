package com.currency.exchange.service;

import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.service.bo.CurrencyConversion;
import com.currency.exchange.service.bo.CurrencySupport;
import com.currency.exchange.exception.CurrencyNotFoundException;
import com.currency.exchange.service.bo.CurrencyExchange;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CurrencyExchangeService {

  /**
   * Calculates the exchange rate in EUR(euro) for a given currency.
   * If necessary details are missing from the consumer then {@link InvalidCurrencyException} is thrown.
   * If the details are incorrect from the consumer then {@link CurrencyNotFoundException} is thrown.
   *
   * @param from
   * @return
   * @throws InvalidCurrencyException
   * @throws CurrencyNotFoundException
   */
  CurrencyExchange getExchangeRateToEuro(String from) throws InvalidCurrencyException, CurrencyNotFoundException;

  /**
   * Calculates the exchange rate of one currency to another for given pair.
   * If necessary details are missing from the consumer then {@link InvalidCurrencyException} is thrown.
   * If the details are incorrect from the consumer then {@link CurrencyNotFoundException} is thrown.
   *
   *
   * @param from
   * @param to
   * @return {@link CurrencyExchange}
   * @throws InvalidCurrencyException
   * @throws CurrencyNotFoundException
   */
  CurrencyExchange getExchangeRateFromCurrencyPairs(String from, String to) throws InvalidCurrencyException, CurrencyNotFoundException;

  /**
   *
   * Get all the supported currencies and no times it's requested.
   *
   * @return
   */
  List<CurrencySupport> getSupportedCurrenciesAndNoOfTimesRequested();

  /**
   * Converts from one currency to another based on the quantity provided.
   * If necessary details are missing from the consumer then {@link InvalidCurrencyException} is thrown.
   * If the details are incorrect from the consumer then {@link CurrencyNotFoundException} is thrown.
   *
   * @param from
   * @param to
   * @param quantity
   * @return
   * @throws InvalidCurrencyException
   * @throws CurrencyNotFoundException
   */
  CurrencyConversion getCurrencyConversion(String from, String to, BigDecimal quantity) throws InvalidCurrencyException, CurrencyNotFoundException;

  /**
   *
   * Retrieves the link to the interactive chart.
   * If the details are incorrect from the consumer then {@link CurrencyNotFoundException} is thrown.
   *
   * @param currency
   * @return
   * @throws CurrencyNotFoundException
   */
  Map<String, String> getCurrencyPairLink (String currency) throws CurrencyNotFoundException;

}
