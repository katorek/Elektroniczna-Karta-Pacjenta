package app.connection;

import app.model.LocalPatient;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.dstu3.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Wojciech Jaronski
 */

public class HapiConnection {
    private static final String CONNECTION_URL = "http://localhost:8080/baseDstu3/";

    private IGenericClient client;
    private FhirContext context;


    private GenericHapi<Patient> patientGenericHapi;
    private GenericHapi<Medication> medicationGenericHapi;


    private List<Patient> patients;

    public HapiConnection() {
        this(CONNECTION_URL);
    }

    public HapiConnection(String customUrl) {
        context = FhirContext.forDstu3();
        context.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);

        client = context.newRestfulGenericClient(customUrl);

        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogRequestSummary(true);
        loggingInterceptor.setLogRequestBody(true);

        client.registerInterceptor(loggingInterceptor);

        patientGenericHapi = new GenericHapi<>(this, Patient.class);
    }


    public List<LocalPatient> getAllPatients() {
        return patientGenericHapi.getList().stream().map(LocalPatient::new).collect(Collectors.toList());
    }


    IGenericClient getClient() {
        return client;
    }

    public void downloadEverythingAboutPatient(LocalPatient patient) {

        Parameters outParams = client
                .operation()
                .onInstance(new IdType("Patient", patient.getPatient().getIdElement().getIdPart()))
                .named("$everything")
                .withNoParameters(Parameters.class).useHttpGet() // No input parameters
                .execute();


        Bundle result = (Bundle) outParams.getParameterFirstRep().getResource();

        while (result.getLink("next") != null) {
            result.getEntry().forEach(e -> {
                Resource res = e.getResource();
                switch (res.getClass().getSimpleName()) {
                    case "MedicationRequest": {
                        patient.put((MedicationRequest) res);
                        break;
                    }
                    case "Observation": {
                        patient.put((Observation) res);
                        break;
                    }
                }
            });
            result = client.loadPage().next(result).execute();
        }
    }
}
