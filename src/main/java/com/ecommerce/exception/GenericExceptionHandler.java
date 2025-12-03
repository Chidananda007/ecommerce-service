package com.ecommerce.exception;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GenericExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<GenericFailureResponse> handleRunTimeException(Exception e, Locale locale) {
    log.error("RuntimeException occurred: {}", e.getMessage(), e);
    String convertedMessageCode = getLocaleMessage("error.serverError", null, locale);
    Map<String, String> errors = new HashMap<>();
    errors.put("Invalid Request", e.getMessage());
    var response = new GenericFailureResponse(e.getMessage(), errors);
    return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericFailureResponse> handleGenericException(Exception e, Locale locale) {
    log.error("Exception occurred: {}", e.getMessage(), e);
    String convertedMessageCode = getLocaleMessage("error.serverError", null, locale);
    Map<String, String> errors = new HashMap<>();
    errors.put("could not process the request", e.getMessage());
    var response =
        new GenericFailureResponse(
            "Oops! Something went wrong on our end. Please try again later.", errors);
    return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getLocaleMessage(String localeCode, Object[] args, Locale locale) {
    try {
      return messageSource.getMessage(localeCode, args, locale);
    } catch (NoSuchMessageException noSuchMessageException) {
      return localeCode;
    }
  }

  @Data
  class GenericFailureResponse {

    private final String message;

    private final Map<String, String> reasons;

    @Override
    public int hashCode() {
      return Objects.hash(message, reasons);
    }

    @Override
    public String toString() {
      var sb = new StringBuilder();
      sb.append("class GenericFailureResponse {\n");

      sb.append("    message: ").append(toIndentedString(message)).append("\n");
      sb.append("    reasons: ").append(toIndentedString(reasons)).append("\n");
      sb.append("}");
      return sb.toString();
    }

    private String toIndentedString(Object o) {
      if (o == null) {
        return "null";
      }
      return o.toString().replace("\n", "\n    ");
    }
  }
}
