package pl.radoslawornat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.radoslawornat.model.Author;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.dto.QuotationDto;
import pl.radoslawornat.model.response.CustomHttpResponse;
import pl.radoslawornat.service.QuotationService;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.radoslawornat.generator.QuotationsGenerator.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = QuotationController.class)
class QuotationControllerTest {

    @MockBean
    QuotationService quotationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldFindAllQuotationsMethodReturnQuotationsPageWhenParamsOfPageAndSizeNotPassed() throws Exception {
        Page<Quotation> quotations = generateExamplePageOfBooks();

        when(quotationService.listAllQuotations(0, 25)).thenReturn(quotations);

        String url = "/api/quotations";

        mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(quotations)));

        verify(quotationService).listAllQuotations(0,25);
    }

    @Test
    void shouldFindAllQuotationsMethodReturnQuotationsPageWhenParamsPassed() throws Exception {
        Page<Quotation> quotations = generateExamplePageOfBooks();

        when(quotationService.listAllQuotations(0, 5)).thenReturn(quotations);

        String url = "/api/quotations";

        mockMvc.perform(get(url)
                .param("page", "0")
                .param("size", "5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(quotations)));

        verify(quotationService).listAllQuotations(0,5);
    }

    @Test
    void shouldFindAllQuotationsMethodReturnBadRequestWhenSizeLargerThan1000() throws Exception {
        String errorMessage = "Cannot retrieve more than 1000 quotations. Please pass the correct size";

        CustomHttpResponse httpResponse
                = new CustomHttpResponse(400, BAD_REQUEST, BAD_REQUEST.getReasonPhrase().toUpperCase(), errorMessage);

        String url = "/api/quotations";

        mockMvc.perform(get(url)
                .param("page", "0")
                .param("size", "1001")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(httpResponse)));

        verify(quotationService, never()).listAllQuotations(0,1001);
    }

    @Test
    void shouldSaveQuotationMethodPersistQuotationWhenQuotationDtoIsValid() throws Exception {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);
        Quotation expectedQuotation = generateQuotationWithArgs(content, author);

        when(quotationService.saveQuotation(any(QuotationDto.class), any())).thenReturn(expectedQuotation);

        String url = "/api/quotations";

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(quotationDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(expectedQuotation)));

        verify(quotationService).saveQuotation(any(QuotationDto.class), any());
    }

    @ParameterizedTest
    @MethodSource("setOfInvalidQuotationDtos")
    void shouldSaveBookMethodReturnBadRequestWhenBookDtoIsInvalid(QuotationDto quotationDto) throws Exception {
        String url = "/api/quotations";

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(quotationDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(quotationService, never()).saveQuotation(quotationDto, null);
    }

    @Test
    void shouldSaveBookMethodReturnBadRequestWhenNullIsPassedInBody() throws Exception {
        String url = "/api/quotations";

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(null))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(quotationService, never()).saveQuotation(null, null);
    }

    @Test
    void shouldUpdateQuotationMethodUpdateQuotationWhenQuotationDtoIsValid() throws Exception {
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");
        QuotationDto quotationDto = new QuotationDto(content, author);
        String quotationId = "someQuotationId";
        Quotation expectedQuotation = generateQuotationWithFixedId(quotationId, content, author);

        when(quotationService.saveQuotation(any(QuotationDto.class), any())).thenReturn(expectedQuotation);

        String url = "/api/quotations/"+quotationId;

        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(quotationDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(expectedQuotation)));

        verify(quotationService).saveQuotation(any(QuotationDto.class), any());
    }

    @ParameterizedTest
    @MethodSource("setOfInvalidQuotationDtos")
    void shouldUpdateBookMethodReturnBadRequestWhenBookDtoIsInvalid(QuotationDto quotationDto) throws Exception {
        String quotationId = "someQuotationId";
        String url = "/api/quotations/"+quotationId;

        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(quotationDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(quotationService, never()).saveQuotation(quotationDto, quotationId);
    }

    @Test
    void shouldUpdateBookMethodReturnBadRequestWhenNullIsPassedInBody() throws Exception {
        String quotationId = "someQuotationId";
        String url = "/api/quotations/"+quotationId;

        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(null))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(quotationService, never()).saveQuotation(null, quotationId);
    }

    @Test
    void shouldDeleteQuotationMethodRemoveQuotationWithStatusOk() throws Exception {
        String quotationId = "someQuotationId";

        doNothing().when(quotationService).deleteQuotationById(quotationId);

        String url = "/api/quotations/"+quotationId;

        mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(quotationService).deleteQuotationById(quotationId);
    }

    @Test
    void shouldDeleteQuotationReturnMethodNotAllowedWhenNullIsPassedAsId() throws Exception {
        doNothing().when(quotationService).deleteQuotationById(null);

        String url = "/api/quotations/";

        mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());

        verify(quotationService, never()).deleteQuotationById(null);
    }


    private static Stream<Arguments> setOfInvalidQuotationDtos() {
        String tooLongContent = generateTooLongContent();
        String content = "Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.";
        Author author = new Author("Winston", "Churchill");

        return Stream.of(
                Arguments.of(new QuotationDto(null, author)),
                Arguments.of(new QuotationDto("", author)),
                Arguments.of(new QuotationDto(tooLongContent, author)),
                Arguments.of(new QuotationDto(content, new Author(null, "Churchill"))),
                Arguments.of(new QuotationDto(content, new Author("", "Churchill"))),
                Arguments.of(new QuotationDto(content, new Author("Winston", null))),
                Arguments.of(new QuotationDto(content, new Author("Winston", "")))
        );
    }


}