package app.model;

import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;

import java.util.*;

/**
 * Created by Wojciech Jaronski
 */

public class LocalRegister {
    private HashMap<Date, ArrayList<MyValue>> register;
    private HashMap<String, TreeMap<Date, Integer>> charts;

    public LocalRegister() {
        register = new HashMap<>();
        charts = new HashMap<>();
    }

    private void put(MyValue val) {
        register.computeIfAbsent(val.getDate(), k -> new ArrayList<>());
        register.get(val.getDate()).add(val);
    }

    private void putChart(MyValue val) {
        charts.computeIfAbsent(val.getString(), s -> new TreeMap<>());
        charts.get(val.getString()).put(val.getDate(), val.getNumber());
    }

    void put(MedicationRequest resource) {
        try {
            String name = "Authored: " + resource.getMedicationCodeableConcept().getText();
            String status = ", status: " + resource.getStatus().toString();
            String intent = ", intent: " + resource.getIntent().toString();
            String result = String.join("", name, status, intent);

            Date date = resource.getAuthoredOn();
            put(new MyValue(result, date));

        } catch (Exception e) {
            System.err.println("MedicationRequest: " + e.getLocalizedMessage());
        }
    }

    void put(Observation obs) {
        Date date = null;
        String value = "";
        Integer number = -1;
        try {
            date = obs.getEffectiveDateTimeType().getValue();

            value = "(" + obs.getValueQuantity().getValue().toString() + " " + obs.getValueQuantity().getUnit() + ")";
            number = obs.getValueQuantity().getValue().intValue();

        } catch (Exception e) {
            System.err.println("Observation: " + e.getLocalizedMessage());
        }

        String name = "";
        String observationName = obs.getCode().getText();
        if (date != null && !value.equals("")) {
            name = "Observation: " + observationName + ", value: " + value;
            putChart(new MyValue(observationName, number, date));
        } else {
            name = observationName + ": " + obs.getValue();
        }
        if (!name.contains("null")) {
            put(new MyValue(name, date));
        }


    }

    public HashMap<Date, ArrayList<MyValue>> getRegister() {
        return register;
    }

    public HashMap<String, TreeMap<Date, Integer>> getCharts() {
        return charts;
    }

}
