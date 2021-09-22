package pl.radoslawornat.model.response;

import lombok.Getter;
import pl.radoslawornat.model.Author;

@Getter
public final class QuotationResource {
    private final String id;
    private final String content;
    private final Author author;

    public QuotationResource(String id, String content, Author author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }
}
