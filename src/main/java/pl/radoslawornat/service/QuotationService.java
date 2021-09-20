package pl.radoslawornat.service;

import org.springframework.data.domain.Page;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.dto.QuotationDto;

public interface QuotationService {
    Page<Quotation> listAllQuotations(int pageNumber, int pageSize);
    Quotation saveQuotation(QuotationDto quotationDto, String quotationId);
    void deleteQuotationById(String quotationId);
}
