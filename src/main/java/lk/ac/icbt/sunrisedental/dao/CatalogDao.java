package lk.ac.icbt.sunrisedental.dao;

import lk.ac.icbt.sunrisedental.model.Dentist;
import lk.ac.icbt.sunrisedental.model.Treatment;
import java.util.Optional;

public interface CatalogDao {
    Optional<Dentist> findDentist(long id);
    Optional<Treatment> findTreatment(long id);
}
