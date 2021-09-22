package pl.radoslawornat.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.dto.QuotationDto;
import pl.radoslawornat.model.exception.QuotationAlreadyExistsException;
import pl.radoslawornat.model.exception.QuotationNotFoundException;
import pl.radoslawornat.model.exception.QuotationServiceException;
import pl.radoslawornat.model.mapper.QuotationMapper;
import pl.radoslawornat.model.response.QuotationResource;
import pl.radoslawornat.repository.QuotationRepository;
import pl.radoslawornat.service.QuotationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationMapper quotationMapper;

    public QuotationServiceImpl(QuotationRepository quotationRepository, QuotationMapper quotationMapper) {
        this.quotationRepository = quotationRepository;
        this.quotationMapper = quotationMapper;
    }

    @Override
    public Page<QuotationResource> listAllQuotations(int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Quotation> quotations = quotationRepository.findAll(pageable);
            List<QuotationResource> quotationResourceList = quotations.getContent().stream()
                    .map(quotation -> quotationMapper.map(quotation, QuotationResource.class))
                    .collect(Collectors.toList());
            return paginateQuotations(quotationResourceList, quotations, pageNumber);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage = "Problem occurred by attempt to list quotations";
            log.error(errorMessage + " due to: " + exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    @Override
    public QuotationResource saveQuotation(QuotationDto quotationDto) {
        try {
            Quotation quotation = new Quotation(quotationDto);
            Quotation validQuotation = validateQuotationToSave(quotation);
            log.info("Attempt to save new quotation of author: {} {}",
                    validQuotation.getAuthor().getFirstName(), validQuotation.getAuthor().getLastName());
            Quotation savedQuotation = quotationRepository.save(validQuotation);
            return quotationMapper.map(savedQuotation, QuotationResource.class);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage = "Problem occurred by attempt to save new quotation";
            log.error(errorMessage + " due to: " + exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    @Override
    public QuotationResource updateQuotation(QuotationDto quotationDto, String quotationId) {
        try {
            Quotation quotation = new Quotation(quotationDto);
            Quotation validQuotation = validateQuotationToUpdate(quotation, quotationId);
            quotation.setId(quotationId);
            log.info("Attempt to update quotation with id: {}", quotationId);
            Quotation updatedQuotation = quotationRepository.save(validQuotation);
            return quotationMapper.map(updatedQuotation, QuotationResource.class);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage =
                    String.format("Problem occurred by attempt to update quotation with id: {}", quotationId);
            log.error(errorMessage + " due to: " + exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    @Override
    public void deleteQuotationById(String quotationId) {
        try {
            if (!quotationRepository.existsById(quotationId)) {
                String errorMessage =
                        String.format("Cannot find quotation with id: %s to delete quotation", quotationId);
                log.info(errorMessage);
                throw new QuotationNotFoundException(errorMessage);
            }
            log.info("Attempt to delete quotation with id: {}", quotationId);
            quotationRepository.deleteById(quotationId);
        } catch (NonTransientDataAccessException exc) {
            String errorMessage =
                    String.format("Problem occurred by attempt to delete quotation with id %s", quotationId);
            log.error(errorMessage + " due to: " + exc.getMessage());
            throw new QuotationServiceException(errorMessage);
        }
    }

    private Quotation validateQuotationToUpdate(Quotation quotation, String quotationId) {
        if (nonNull(quotationId)) {
            if (!quotationRepository.existsById(quotationId)) {
                String errorMessage = String.format("Cannot find quotation with id %s to update quotation", quotationId);
                log.info(errorMessage);
                throw new QuotationNotFoundException(errorMessage);
            }
        } else {
            String errorMessage = "Attempt to update quotation with passed null id";
            log.info(errorMessage);
            throw new QuotationServiceException(errorMessage);
        }
        return quotation;
    }

    private Quotation validateQuotationToSave(Quotation quotation) {
        if (checkWhetherQuotationAlreadyExists(quotation)) {
            String warningMessage = "Attempt to add quotation that already exists";
            log.info(warningMessage);
            throw new QuotationAlreadyExistsException(warningMessage);
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

    private Page<QuotationResource> paginateQuotations(List<QuotationResource> quotationsResources,
                                                       Page<Quotation> quotationsPage,
                                                       int page) {
        PagedListHolder<QuotationResource> listHolder = new PagedListHolder<>(quotationsResources);
        listHolder.setPage(page);
        listHolder.setPageSize(quotationsPage.getSize());
        return new PageImpl<>(listHolder.getPageList(), quotationsPage.getPageable(), quotationsPage.getTotalElements());
    }
}
