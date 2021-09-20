package pl.radoslawornat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.radoslawornat.model.Author;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class QuotationDto {

    @NotBlank(message = "Content cannot be null and cannot contain only whitespaces")
    @Size(max = 1000, message = "Content cannot be longer than 1000 characters")
    private String content;

    @NotNull(message = "Author cannot be null")
    @Valid
    private Author author;
}
