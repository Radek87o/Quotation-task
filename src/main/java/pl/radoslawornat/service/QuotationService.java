package pl.radoslawornat.service;

import org.springframework.data.domain.Page;
import pl.radoslawornat.model.dto.QuotationDto;
import pl.radoslawornat.model.response.QuotationResource;

public interface QuotationService {
    Page<QuotationResource> listAllQuotations(int pageNumber, int pageSize);

    QuotationResource saveQuotation(QuotationDto quotationDto);

    QuotationResource updateQuotation(QuotationDto quotationDto, String quotationId);

    void deleteQuotationById(String quotationId);
}
