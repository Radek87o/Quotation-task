package pl.radoslawornat.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.radoslawornat.model.exception.QuotationAlreadyExistsException;
import pl.radoslawornat.model.exception.QuotationNotFoundException;
import pl.radoslawornat.model.response.CustomHttpResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a %s request";

    @ExceptionHandler(value = QuotationAlreadyExistsException.class)
    public ResponseEntity<CustomHttpResponse> quotationAlreadyExistsException(QuotationAlreadyExistsException exc){
        return createHttpResponse(BAD_REQUEST, exc.getMessage());
    }

    @ExceptionHandler(value = QuotationNotFoundException.class)
    public ResponseEntity<CustomHttpResponse> quotationNotFoundException(QuotationNotFoundException exc){
        return createHttpResponse(NOT_FOUND, exc.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(Exception e, WebRequest request) {
        log.error("Handling {} due to {}", e.getClass().getSimpleName(), e.getMessage());
        e.printStackTrace();
        return createJsonResponse(e, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exc, HttpHeaders headers, HttpStatus status, WebRequest request) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpMethod httpSupportedMethod = Objects.requireNonNull(exc.getSupportedHttpMethods()).iterator().next();
        ResponseEntity<CustomHttpResponse> httpResponse = createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, httpSupportedMethod.name()));
        return new ResponseEntity<>(httpResponse.getBody(), headers, METHOD_NOT_ALLOWED);
    }


    private ResponseEntity<CustomHttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CustomHttpResponse httpResponse = new CustomHttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message);
        return new ResponseEntity<>(httpResponse, headers, httpStatus);
    }

    private ResponseEntity<Object> createJsonResponse(Exception e, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (e instanceof ResponseStatusException) {
            return new ResponseEntity<>(createExceptionBody(((ResponseStatusException) e).getStatus(), ((ResponseStatusException) e).getReason(), request.getDescription(false)), headers, ((ResponseStatusException) e).getStatus());
        }
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(createExceptionBody(status, "An unexpected error occurred", request.getDescription(false)), headers, status);
    }

    private Map<String, Object> createExceptionBody(HttpStatus status, String message, String path) {
        Map<String, Object> exceptionBody = new LinkedHashMap<>();
        exceptionBody.put("timestamp", LocalDateTime.now());
        exceptionBody.put("status", status.value());
        exceptionBody.put("error", status.getReasonPhrase());
        exceptionBody.put("message", message);
        exceptionBody.put("path", path);
        return exceptionBody;
    }
}
