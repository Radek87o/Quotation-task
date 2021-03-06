package pl.radoslawornat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.radoslawornat.model.dto.QuotationDto;
import pl.radoslawornat.model.response.CustomHttpResponse;
import pl.radoslawornat.model.response.QuotationResource;
import pl.radoslawornat.service.QuotationService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.radoslawornat.controller.ResponseHelper.createCreatedResponse;
import static pl.radoslawornat.controller.ResponseHelper.createOkResponse;

@Slf4j
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    private final QuotationService quotationService;
    private final int defaultSize;
    private final int maxQuotationsListSize;
    private final int minQuotationsListParamValue;

    public QuotationController(QuotationService quotationService,
                               @Value("${quotations.default-size}") int defaultSize,
                               @Value("${quotations.max-quotations-size}") int maxQuotationsListSize,
                               @Value("${quotations.min-quotations-list-param-value}") int minQuotationsListParamValue) {
        this.quotationService = quotationService;
        this.defaultSize = defaultSize;
        this.maxQuotationsListSize = maxQuotationsListSize;
        this.minQuotationsListParamValue = minQuotationsListParamValue;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllQuotations(@RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber,
                                               @RequestParam(name = "size", required = false, defaultValue = "25") int pageSize) {
        pageNumber = pageNumber < minQuotationsListParamValue ? minQuotationsListParamValue : pageNumber;
        pageSize = pageSize <= minQuotationsListParamValue ? defaultSize : pageSize;
        if (pageSize > maxQuotationsListSize) {
            log.info("Attempt to retrieve more than {} quotations", maxQuotationsListSize);
            String responseMessage =
                    String.format("Cannot retrieve more than %s quotations. Please pass the correct size", maxQuotationsListSize);
            return response(BAD_REQUEST, responseMessage);
        }
        Page<QuotationResource> quotations = quotationService.listAllQuotations(pageNumber, pageSize);
        return createOkResponse(quotations);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveQuotation(@Valid @RequestBody QuotationDto quotation) {
        QuotationResource quotationToSave = quotationService.saveQuotation(quotation);
        return createCreatedResponse(quotationToSave);
    }

    @PutMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateQuotation(@Valid @RequestBody QuotationDto quotation,
                                             @PathVariable("id") String quotationId) {
        QuotationResource updatedQuotation = quotationService.updateQuotation(quotation, quotationId);
        return createOkResponse(updatedQuotation);
    }

    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteQuotation(@PathVariable("id") String quotationId) {
        quotationService.deleteQuotationById(quotationId);
        return response(OK, String.format("Quotation with id: %s was successfully deleted", quotationId));
    }

    private ResponseEntity<CustomHttpResponse> response(HttpStatus status, String message) {
        CustomHttpResponse httpResponse = new CustomHttpResponse(
                status.value(), status, status.getReasonPhrase().toUpperCase(), message);
        return new ResponseEntity<>(httpResponse, status);
    }

}
