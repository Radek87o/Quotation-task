package pl.radoslawornat.model.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import pl.radoslawornat.model.Quotation;
import pl.radoslawornat.model.response.QuotationResource;

@Component
public class QuotationMapper extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(Quotation.class, QuotationResource.class)
                .byDefault()
                .register();
    }
}
