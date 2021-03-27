package com.currency.exchange.advice;

import com.currency.exchange.exception.InvalidCurrencyException;
import com.currency.exchange.exception.CurrencyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CurrencyExchangeErrorResponseAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyExchangeErrorResponseAdvice.class);

    private static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    /**
     * Handles Exception when method argument is not the expected type.
     *
     * @param ex
     *            {@link MethodArgumentTypeMismatchException}
     * @param request
     *            {@link HttpServletRequest}
     * @return {@link ResponseEntity} containing standard body in case of errors
     */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public HttpEntity<CurrencyExchangeErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                                      HttpServletRequest request) {

        String errorMessage = String.format("Incorrect value '%s' for field '%s'. Expected value of type '%s'",
                ex.getValue(), ex.getName(), ex.getParameter().getParameterType().getTypeName());

        CurrencyExchangeErrorResponse error = new CurrencyExchangeErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), null,
                "Parameter type mismatch", errorMessage, request.getRequestURI());

        return new ResponseEntity<>(error, overrideContentType(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles the exception when input currency attributes are invalid.
     *
     * @param ex{@link
     * 			{@link InvalidCurrencyException}}
     * @param request
     *            {@link HttpServletRequest}
     * @return {@link ResponseEntity} containing standard body in case of errors
     */
    @ExceptionHandler({ InvalidCurrencyException.class })
    public ResponseEntity<CurrencyExchangeErrorResponse> handleInvalidCurrencyException(InvalidCurrencyException ex,
                                                                         HttpServletRequest request) {

        LOGGER.debug("Validation failed because of ", ex.getMessage());


        CurrencyExchangeErrorResponse errorResp = new CurrencyExchangeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
                "Invalid currencies", ex.getMessage(), request.getRequestURI());

        return new ResponseEntity<>(errorResp, overrideContentType(),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the exception when input currency attributes are invalid.
     *
     * @param ex{@link
     * 			{@link CurrencyNotFoundException}}
     * @param request
     *            {@link HttpServletRequest}
     * @return {@link ResponseEntity} containing standard body in case of errors
     */
    @ExceptionHandler({ CurrencyNotFoundException.class })
    public ResponseEntity<CurrencyExchangeErrorResponse> handleCurrencyNotFoundException(CurrencyNotFoundException ex,
                                                                                        HttpServletRequest request) {

        LOGGER.debug("Currency Not Found because of ", ex.getMessage());


        CurrencyExchangeErrorResponse errorResp = new CurrencyExchangeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null,
                "Currency Not found", ex.getMessage(), request.getRequestURI());

        return new ResponseEntity<>(errorResp, overrideContentType(),
                HttpStatus.NOT_FOUND);
    }

    /**
     * fall-back handler – a catch-all type of logic that deals with all other
     * exceptions that don’t have specific handlers.
     *
     * @param ex{@link
     * 			Exception}
     */
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<CurrencyExchangeErrorResponse> handleAll(Exception ex, HttpServletRequest request) {

        LOGGER.error("An unexpected error occurred", ex);

        CurrencyExchangeErrorResponse errorResp = new CurrencyExchangeErrorResponse(HttpStatus.BAD_REQUEST.value(), null,
                "Failed:","An unexpected error has occurred", request.getRequestURI());

        return new ResponseEntity<>(errorResp, overrideContentType(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpHeaders overrideContentType() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, APPLICATION_PROBLEM_JSON);
        return httpHeaders;
    }
}
