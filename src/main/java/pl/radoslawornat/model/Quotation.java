package pl.radoslawornat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import pl.radoslawornat.model.dto.QuotationDto;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Quotation {

    @Id
    @GenericGenerator(name = "quotation_id", strategy = "pl.radoslawornat.model.generator.CustomIdGenerator")
    @GeneratedValue(generator = "quotation_id")
    private String id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Embedded
    private Author author;

    public Quotation(QuotationDto dto) {
        this.content=dto.getContent();
        this.author=dto.getAuthor();
    }
}
