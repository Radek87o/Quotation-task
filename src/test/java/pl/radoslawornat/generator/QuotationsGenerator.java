package pl.radoslawornat.generator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import pl.radoslawornat.model.Author;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.response.QuotationResource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class QuotationsGenerator {

    public static Page<QuotationResource> generateExamplePageOfQuotationResources() {
        List<QuotationResource> quotations = Arrays.asList(
                generateQuotationResource("Główną nauką płynącą z historii jest to, " +
                                "że ludzkość niczego się nie uczy.",
                        new Author("Winston", "Churchill")),
                generateQuotationResource("Dżentelmen – to człowiek, który nie rani " +
                                "cudzych uczuć, chyba że umyślnie.",
                        new Author("Oscar", "Wilde")),
                generateQuotationResource("Demagogia – to umiejętność ubierania najbardziej " +
                                "lichych idei w najwznioślejsze słowa.",
                        new Author("Abraham", "Lincoln")),
                generateQuotationResource("Każda praca jest możliwa do wykonania jeśli podzielić " +
                                "ją na małe odcinki.",
                        new Author("Abraham", "Lincoln")),
                generateQuotationResource("Im bardziej się człowiek starzeje, tym mocniej czuje, " +
                                "że umiejętność cieszenia się chwilą bieżącą jest cennym darem podobnym do stanu łaski.",
                        new Author("Maria", "Skłodowska-Curie"))
        );
        return new PageImpl<>(quotations);
    }

    public static Page<Quotation> generateExamplePageOfQuotations() {
        List<Quotation> quotations = Arrays.asList(
                generateQuotation("Główną nauką płynącą z historii jest to, że ludzkość niczego się nie uczy.",
                        new Author("Winston", "Churchill")),
                generateQuotation("Dżentelmen – to człowiek, który nie rani cudzych uczuć, chyba że umyślnie.",
                        new Author("Oscar", "Wilde")),
                generateQuotation("Demagogia – to umiejętność ubierania najbardziej " +
                                "lichych idei w najwznioślejsze słowa.",
                        new Author("Abraham", "Lincoln")),
                generateQuotation("Każda praca jest możliwa do wykonania jeśli podzielić ją na małe odcinki.",
                        new Author("Abraham", "Lincoln")),
                generateQuotation("Im bardziej się człowiek starzeje, tym mocniej czuje, że umiejętność " +
                                "cieszenia się chwilą bieżącą jest cennym darem podobnym do stanu łaski.",
                        new Author("Maria", "Skłodowska-Curie"))
        );
        return new PageImpl<>(quotations);
    }

    public static Quotation generateQuotationWithFixedId(String id, String content, Author author) {
        Quotation quotation = generateQuotation(content, author);
        quotation.setId(id);
        return quotation;
    }

    public static QuotationResource generateQuotationResourceWithFixedId(String id, String content, Author author) {
        return new QuotationResource(id, content, author);
    }

    public static Quotation generateQuotationWithArgs(String content, Author author) {
        return generateQuotation(content, author);
    }

    public static QuotationResource generateQuotationResourceWithArgs(String content, Author author) {
        return generateQuotationResource(content, author);
    }

    public static String generateTooLongContent() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 1001;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    private static Quotation generateQuotation(String content, Author author) {
        Quotation quotation = new Quotation();
        quotation.setId(UUID.randomUUID().toString());
        quotation.setAuthor(author);
        quotation.setContent(content);
        return quotation;
    }

    private static QuotationResource generateQuotationResource(String content, Author author) {
        return new QuotationResource(UUID.randomUUID().toString(), content, author);
    }
}
