package com.currency.exchange.rest;

import com.currency.exchange.service.bo.CurrencyConversion;
import com.currency.exchange.service.bo.CurrencySupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.currency.exchange.service.bo.CurrencyExchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyExchangeResourceTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void getExchangeRateToEuro() throws Exception {
        mockMvc.perform(get("/currency/exchange/rate/from/USD/to/EUR").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\",\"fromCountry\":\"United States\",\"toCountry\":\"Europe\",\"exchangeRate\":0.843}"));
    }

    @Test
    public void getExchangeRateToOtherCurrencyPairs() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/currency/exchange/rate/from/USD/to/HUF").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        CurrencyExchange exchange = mapper.readValue(mvcResult.getResponse().getContentAsString(), CurrencyExchange.class);
        assertThat(exchange).isNotNull();
        assertThat(exchange).extracting("fromCurrency").isEqualTo("USD");
        assertThat(exchange).extracting("toCurrency").isEqualTo("HUF");
        assertThat(exchange).extracting("exchangeRate").isEqualTo(new BigDecimal(309.24).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void getCurrencyConversion() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/exchange/conversion/from/USD/to/HUF/quantity/15").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        CurrencyConversion exchange = mapper.readValue(mvcResult.getResponse().getContentAsString(), CurrencyConversion.class);
        assertThat(exchange).isNotNull();
        assertThat(exchange).extracting("from").isEqualTo("USD");
        assertThat(exchange).extracting("to").isEqualTo("HUF");
        assertThat(exchange).extracting("exchangeRate").isEqualTo(new BigDecimal(309.24).setScale(2, RoundingMode.HALF_UP));
        assertThat(exchange).extracting("calculateAmount").isEqualTo(new BigDecimal(4638.60).setScale(2, RoundingMode.HALF_UP));
        assertThat(exchange).extracting("quantity").isEqualTo(new BigDecimal(15));
    }

    @Test
    public void getSupportedCurrencies() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/currency/exchange/supported/currencies").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        List<CurrencySupport> currencySupport = Arrays.asList(mapper.readValue(mvcResult.getResponse().getContentAsString(), CurrencySupport[].class));
        assertThat(currencySupport).isNotEmpty();
        assertThat(currencySupport).extracting("currency").isEqualTo(Stream.of("AUD","SGD","JPY","GBP","CZK","USD","CAD","HUF","NZD","INR").collect(Collectors.toList()));
    }

    @Test
    public void getCurrencyPairChart() throws Exception {
        mockMvc.perform(get("/exchange/linkTo/EUR-USD"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("{\"currencyPair\":\"https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/eurofxref-graph-usd.en.html\"}"));
    }




}
