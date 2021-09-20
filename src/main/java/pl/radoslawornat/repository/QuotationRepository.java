package pl.radoslawornat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.radoslawornat.model.Quotation;

import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, String> {
    Optional<Quotation> findByContentAndAuthor_FirstNameAndAuthor_LastNameIgnoreCase(String content, String firstName, String lastName);
}
