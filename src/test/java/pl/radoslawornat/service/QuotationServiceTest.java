package pl.radoslawornat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.radoslawornat.model.Author;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.dto.QuotationDto;
import pl.radoslawornat.model.exception.QuotationAlreadyExistsException;
import pl.radoslawornat.model.exception.QuotationNotFoundException;
import pl.radoslawornat.model.exception.QuotationServiceException;
import pl.radoslawornat.repository.QuotationRepository;
import pl.radoslawornat.service.impl.QuotationServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pl.radoslawornat.generator.QuotationsGenerator.*;

@ExtendWith(MockitoExtension.class)
class QuotationServiceTest {

    @Mock
    QuotationRepository quotationRepository;

    QuotationService quotationService;

    @BeforeEach
    void setup() {
        quotationService = new QuotationServiceImpl(quotationRepository);
    }

    @Test
    void listAllQuotationsMethodShouldReturnCorrectPageOfQuotations() {
        Page<Quotation> quotations = generateExamplePageOfBooks();
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(quotationRepository.findAll(pageRequest)).thenReturn(quotations);

        Page<Quotation> result = quotationService.listAllQuotations(0, 5);
        assertEquals(quotations, result);

        verify(quotationRepository).findAll(pageRequest);
    }

    @Test
    void listAllQuotationsMethodShouldThrowQuotationServiceExceptionWhenNonTransientDataAccessExceptionOccurs() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        doThrow(new NonTransientDataAccessException(""){}).when(quotationRepository).findAll(pageRequest);

        assertThrows(QuotationServiceException.class, () -> quotationService.listAllQuotations(0, 5));

        verify(quotationRepository).findAll(pageRequest);
    }

    @Test
    void saveQuotationMethodShouldReturnQuotationWhenCorrectQuotationDtoPassed() {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);
        Quotation expectedQuotation = new Quotation(quotationDto);
        when(quotationRepository.findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                content, author.getFirstName(), author.getLastName()
        )).thenReturn(Optional.empty());
        when(quotationRepository.save(any(Quotation.class))).thenReturn(expectedQuotation);

        Quotation result = quotationService.saveQuotation(quotationDto, null);

        assertEquals(expectedQuotation.getAuthor().getFirstName(), result.getAuthor().getFirstName());
        assertEquals(expectedQuotation.getAuthor().getLastName(), result.getAuthor().getLastName());
        assertEquals(expectedQuotation.getContent(), result.getContent());

        verify(quotationRepository).findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                content, author.getFirstName(), author.getLastName()
        );
        verify(quotationRepository).save(any(Quotation.class));
    }

    @Test
    void saveQuotationMethodShouldUpdateExistingQuotationWhenCorrectIdIsPassed() {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);

        String quotationId = "someQuotationId";
        Quotation quotation = generateQuotationWithFixedId(quotationId, content, author);

        when(quotationRepository.existsById(quotationId)).thenReturn(true);
        when(quotationRepository.save(any(Quotation.class))).thenReturn(quotation);

        Quotation result = quotationService.saveQuotation(quotationDto, quotationId);

        assertEquals(quotationDto.getAuthor().getFirstName(), result.getAuthor().getFirstName());
        assertEquals(quotationDto.getAuthor().getLastName(), result.getAuthor().getLastName());
        assertEquals(quotationDto.getContent(), result.getContent());
        assertEquals(quotation.getId(), result.getId());

        verify(quotationRepository).existsById(quotationId);
        verify(quotationRepository).save(any(Quotation.class));
    }

    @Test
    void saveQuotationMethodShouldThrowQuotationNotFoundExceptionWhenQuotationForPassedIdDoesNotExist() {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);
        String quotationId = "someQuotationId";

        when(quotationRepository.existsById(quotationId)).thenReturn(false);

        assertThrows(QuotationNotFoundException.class, () -> quotationService.saveQuotation(quotationDto, quotationId));

        verify(quotationRepository).existsById(quotationId);
    }

    @Test
    void saveQuotationMethodShouldThrowQuotationAlreadyExistsExceptionWhenPassedQuotationAlreadyExists() {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);
        Quotation existingQuotation = generateQuotationWithArgs(content, author);

        when(quotationRepository.findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                content, author.getFirstName(), author.getLastName()
        )).thenReturn(Optional.of(existingQuotation));

        assertThrows(QuotationAlreadyExistsException.class, ()-> quotationService.saveQuotation(quotationDto, null));

        verify(quotationRepository).findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                content, author.getFirstName(), author.getLastName()
        );
    }

    @Test
    void saveQuotationMethodShouldThrowQuotationServiceExceptionWhenNonTransientDataAccessExceptionOccurs() {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);
        when(quotationRepository.findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                content, author.getFirstName(), author.getLastName()
        )).thenReturn(Optional.empty());
        doThrow(new NonTransientDataAccessException(""){}).when(quotationRepository).save(any(Quotation.class));

        assertThrows(QuotationServiceException.class, () -> quotationService.saveQuotation(quotationDto, null));

        verify(quotationRepository).findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(
                content, author.getFirstName(), author.getLastName()
        );
        verify(quotationRepository).save(any(Quotation.class));
    }

    @Test
    void deleteQuotationByIdMethodShouldRemoveQuotationWhenCorrectQuotationIdIsPassed() {
        String quotationId = "someQuotationId";

        when(quotationRepository.existsById(quotationId)).thenReturn(true);
        doNothing().when(quotationRepository).deleteById(quotationId);

        quotationService.deleteQuotationById(quotationId);

        verify(quotationRepository).existsById(quotationId);
        verify(quotationRepository).deleteById(quotationId);
    }

    @Test
    void deleteQuotationByIdMethodShouldThrowQuotationNotFoundExceptionWhenQuotationForPassedIdDoesNotExist() {
        String quotationId = "someQuotationId";

        when(quotationRepository.existsById(quotationId)).thenReturn(false);

        assertThrows(QuotationNotFoundException.class, ()->quotationService.deleteQuotationById(quotationId));

        verify(quotationRepository).existsById(quotationId);
    }

    @Test
    void deleteQuotationByIdMethodShouldThrowQuotationServiceExceptionWhenNonTransientDataAccessExceptionOccurs() {
        String quotationId = "someQuotationId";

        when(quotationRepository.existsById(quotationId)).thenReturn(true);
        doThrow(new NonTransientDataAccessException(""){}).when(quotationRepository).deleteById(quotationId);

        assertThrows(QuotationServiceException.class, () -> quotationService.deleteQuotationById(quotationId));

        verify(quotationRepository).existsById(quotationId);
        verify(quotationRepository).deleteById(quotationId);
    }
}