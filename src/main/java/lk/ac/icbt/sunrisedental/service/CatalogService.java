package lk.ac.icbt.sunrisedental.service;

import java.math.BigDecimal;
import java.util.List;
import lk.ac.icbt.sunrisedental.dao.CatalogDao;
import lk.ac.icbt.sunrisedental.exception.ValidationException;
import lk.ac.icbt.sunrisedental.model.Dentist;
import lk.ac.icbt.sunrisedental.model.Treatment;

public class CatalogService {
    private final CatalogDao catalogDao;

    public CatalogService(CatalogDao catalogDao) { this.catalogDao = catalogDao; }

    public List<Dentist> dentists() { return catalogDao.findActiveDentists(); }
    public List<Treatment> treatments() { return catalogDao.findActiveTreatments(); }

    public Dentist addDentist(String fullName) {
        if (fullName == null || fullName.isBlank()) throw new ValidationException("Dentist name is required");
        return catalogDao.saveDentist(fullName.trim());
    }

    public Treatment addTreatment(String name, BigDecimal price) {
        if (name == null || name.isBlank()) throw new ValidationException("Treatment name is required");
        if (price == null || price.signum() < 0) throw new ValidationException("Treatment price must be zero or more");
        return catalogDao.saveTreatment(name.trim(), price);
    }
}
