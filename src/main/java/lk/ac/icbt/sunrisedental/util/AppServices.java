package lk.ac.icbt.sunrisedental.util;

import lk.ac.icbt.sunrisedental.dao.*;
import lk.ac.icbt.sunrisedental.service.*;

public final class AppServices {
    private static final AppointmentService APPOINTMENTS = new AppointmentService(new JdbcAppointmentDao(), new JdbcCatalogDao(), new JdbcPatientDao());
    private static final CatalogService CATALOG = new CatalogService(new JdbcCatalogDao());
    private static final PatientService PATIENTS = new PatientService(new JdbcPatientDao());
    private static final AuthenticationService AUTH = new AuthenticationService(new JdbcUserDao());
    private static final BillingService BILLING = new BillingService(APPOINTMENTS, new JdbcBillDao(), new JdbcClinicSettingsDao());
    private AppServices() { }
    public static AppointmentService appointments() { return APPOINTMENTS; }
    public static AuthenticationService authentication() { return AUTH; }
    public static BillingService billing() { return BILLING; }
    public static CatalogService catalog() { return CATALOG; }
    public static PatientService patients() { return PATIENTS; }
}
