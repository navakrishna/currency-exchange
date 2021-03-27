package com.currency.exchange.rest;

import com.currency.exchange.advice.CurrencyExchangeErrorResponse;
import com.currency.exchange.exception.CurrencyNotFoundException;
import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.service.CurrencyExchangeService;
import com.currency.exchange.service.bo.CurrencyConversion;
import com.currency.exchange.service.bo.CurrencyExchange;
import com.currency.exchange.service.bo.CurrencySupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class CurrencyExchangeResource {

    private CurrencyExchangeService service;

    public CurrencyExchangeResource(CurrencyExchangeService service) {
        this.service = service;
    }

    @ApiOperation(
            value="Retrieves currency exchange rate from other currencies to EURO, For example 1 USD is 0.843 EUR approximately",
            notes = "Make a GET request to retrieve currency exchanges",
            response = CurrencyExchange.class,
            httpMethod = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved", response = CurrencyExchange.class),
            @ApiResponse(code = 400, message = "If any inputs are missing", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 404, message = "If no currencies found for inputs", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 500, message = "Unexpected Internal Error", response = CurrencyExchangeErrorResponse.class)})
    @GetMapping("/currency/exchange/rate/from/{from}/to/EUR")
    public CurrencyExchange getExchangeRateToEuro(@PathVariable String from) throws InvalidCurrencyException, CurrencyNotFoundException {
        return service.getExchangeRateToEuro(from);
    }

    @ApiOperation(
            value="Retrieves currency exchange rate from two different currency pairs",
            notes = "Make a GET request to retrieve currency exchanges",
            response = CurrencyExchange.class,
            httpMethod = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved", response = CurrencyExchange.class),
            @ApiResponse(code = 400, message = "If any inputs are missing", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 404, message = "If no currencies found for inputs", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 500, message = "Unexpected Internal Error", response = CurrencyExchangeErrorResponse.class)})
    @GetMapping("/currency/exchange/rate/from/{from}/to/{to}")
    public CurrencyExchange getExchangeRateForCurrencyPairs(@PathVariable String from, @PathVariable String to) throws InvalidCurrencyException, CurrencyNotFoundException {
        return service.getExchangeRateFromCurrencyPairs(from,to);
    }

    @ApiOperation(
            value="Retrieves the supported currencies and number of times those currencies are requested",
            notes = "Make a GET request to retrieve supported currencies",
            response = List.class,
            httpMethod = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved", response = CurrencySupport.class),})

    @GetMapping("/currency/exchange/supported/currencies")
    public List<CurrencySupport> getSupportedCurrencies(){
        return service.getSupportedCurrenciesAndNoOfTimesRequested();
    }

    @ApiOperation(
            value="Calculates the currency conversion based on the exchange rate",
            notes = "Make a GET request to retrieve currency conversions",
            response = CurrencySupport.class,
            httpMethod = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved", response = CurrencySupport.class),
            @ApiResponse(code = 400, message = "If any inputs are missing", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 404, message = "If no currencies found for inputs", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 500, message = "Unexpected Internal Error", response = CurrencyExchangeErrorResponse.class)})
    @GetMapping("/exchange/conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion getCurrencyConversion(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) throws InvalidCurrencyException, CurrencyNotFoundException {
        CurrencyConversion currencyConversion = service.getCurrencyConversion(from, to, quantity);
        return currencyConversion;
    }


    @ApiOperation(
            value="Retrieves the currency chart url",
            notes = "Make a GET request to retrieve urls",
            response = Map.class,
            httpMethod = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved", response = Map.class),
            @ApiResponse(code = 400, message = "If any inputs are missing", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 404, message = "If no currencies found for inputs", response = CurrencyExchangeErrorResponse.class),
            @ApiResponse(code = 500, message = "Unexpected Internal Error", response = CurrencyExchangeErrorResponse.class)})
    @GetMapping("/exchange/linkTo/{currencyPair}")
    public Map<String, String> getCurrencyPairLink(@PathVariable String currencyPair) throws CurrencyNotFoundException {
        return service.getCurrencyPairLink(currencyPair);
    }
}
