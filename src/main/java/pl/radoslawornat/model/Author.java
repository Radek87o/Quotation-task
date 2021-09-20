package pl.radoslawornat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
public class Author {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    public Author(){}
}
