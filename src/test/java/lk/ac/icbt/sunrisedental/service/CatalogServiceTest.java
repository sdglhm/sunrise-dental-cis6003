package lk.ac.icbt.sunrisedental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lk.ac.icbt.sunrisedental.dao.CatalogDao;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.Dentist;
import lk.ac.icbt.sunrisedental.model.Treatment;
import org.junit.jupiter.api.Test;

class CatalogServiceTest {
    @Test void addsADentistWithTrimmedName() {
        CatalogService service = new CatalogService(new StubCatalog());
        assertEquals("Dr Perera", service.addDentist("  Dr Perera  ").fullName());
    }

    @Test void rejectsNegativeTreatmentPrice() {
        CatalogService service = new CatalogService(new StubCatalog());
        assertThrows(ValidationException.class, () -> service.addTreatment("Cleaning", new BigDecimal("-1.00")));
    }

    private static class StubCatalog implements CatalogDao {
        public Optional<Dentist> findDentist(long id) { return Optional.empty(); }
        public Optional<Treatment> findTreatment(long id) { return Optional.empty(); }
        public List<Dentist> findActiveDentists() { return List.of(); }
        public List<Treatment> findActiveTreatments() { return List.of(); }
        public Dentist saveDentist(String name) { return new Dentist(1, name, true); }
        public Treatment saveTreatment(String name, BigDecimal price) { return new Treatment(1, name, price, true); }
    }
}
