package com.currency.exchange.exception;

public class CurrencyNotFoundException extends Exception{
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
