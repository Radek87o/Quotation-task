package pl.radoslawornat.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.dto.QuotationDto;
import pl.radoslawornat.model.exception.QuotationAlreadyExistsException;
import pl.radoslawornat.model.exception.QuotationNotFoundException;
import pl.radoslawornat.model.exception.QuotationServiceException;
import pl.radoslawornat.repository.QuotationRepository;
import pl.radoslawornat.service.QuotationService;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;

    public QuotationServiceImpl(QuotationRepository quotationRepository) {
        this.quotationRepository = quotationRepository;
    }

    @Override
    public Page<Quotation> listAllQuotations(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return quotationRepository.findAll(pageable);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage = "Problem occurred by attempt to list quotations";
            log.error(errorMessage+" due to: "+exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    @Override
    public Quotation saveQuotation(QuotationDto quotationDto, String quotationId) {
        try {
            Quotation quotation = new Quotation(quotationDto);
            Quotation validQuotation = validateQuotation(quotation, quotationId);
            log.info("Attempt to save quotation of author: {} {}",
                    validQuotation.getAuthor().getFirstName(), validQuotation.getAuthor().getLastName());
            return quotationRepository.save(validQuotation);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage = String.format("Problem occurred by attempt to save quotation with id %s", quotationId);
            log.error(errorMessage+" due to: "+exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    @Override
    public void deleteQuotationById(String quotationId) {
        try {
            if(!quotationRepository.existsById(quotationId)) {
                String errorMessage = String.format("Cannot find quotation with id: %s to delete quotation", quotationId);
                log.info(errorMessage);
                throw new QuotationNotFoundException(errorMessage);
            }
            log.info("Attempt to delete quotation with id: {}", quotationId);
            quotationRepository.deleteById(quotationId);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage = String.format("Problem occurred by attempt to delete quotation with id %s", quotationId);
            log.error(errorMessage+" due to: "+exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    private Quotation validateQuotation(Quotation quotation, String quotationId) {
        if(nonNull(quotationId)) {
            if(!quotationRepository.existsById(quotationId)) {
                String errorMessage = String.format("Cannot find quotation with id %s to update quotation", quotationId);
                log.info(errorMessage);
                throw new QuotationNotFoundException(errorMessage);
            }
            else {
                quotation.setId(quotationId);
            }
        } else {
            if(checkWhetherQuotationAlreadyExists(quotation)) {
                String warningMessage = "Attempt to add quotation that already exists";
                log.info(warningMessage);
                throw new QuotationAlreadyExistsException(warningMessage);
            }
        }
        return quotation;
    }

    private boolean checkWhetherQuotationAlreadyExists(Quotation quotation) {
        Optional<Quotation> checkedQuotation
                = quotationRepository.findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                quotation.getContent(), quotation.getAuthor().getFirstName(), quotation.getAuthor().getLastName()
        );
        return checkedQuotation.isPresent();
    }
}
