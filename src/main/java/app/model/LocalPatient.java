package app.model;

import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.dstu3.model.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Wojciech Jaronski
 */

public class LocalPatient {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static Map<String, MyPoint> map;

    static {
        map = new HashMap<>();
        map.put("id", new MyPoint("Id", 0, 0));

        map.put("name", new MyPoint("First name", 0, 1));
        map.put("lastName", new MyPoint("Last name", 1, 1));

        map.put("birthDateStr", new MyPoint("Birth Date", 0, 2));
        map.put("gender", new MyPoint("Gender", 1, 2));

        map.put("telephone", new MyPoint("Telephone no.", 0, 3));

        map.put("adress", new MyPoint("Adress", 0, 4));
        map.put("state", new MyPoint("State", 1, 4));

        map.put("street", new MyPoint("Street", 0, 5));
        map.put("city", new MyPoint("City", 1, 5));

        map.put("postCode", new MyPoint("Post code", 0, 6));
        map.put("country", new MyPoint("County", 1, 6));

//        map.put("state", MyPoint.of("State",0, 1));
    }

    private Patient patient;

    private String name;
    private String lastName;
    private Date birthDate;
    private String birthDateStr;
    private String gender;
    private String telephone;
    private String adress;
    private String state;
    private String street;
    private String city;
    private String postCode;
    private String id;


    private String country;
    private LocalRegister register;

    public static Map<String, MyPoint> getFields() {
        return map;
    }

    public LocalPatient(Patient patient) {
        this();
        this.patient = patient;
        id = patient.getIdElement().getIdPart();
        name = obtainName(patient);
        lastName = obtainSecondName(patient);

        city = patient.getAddress().get(0).getCity();
        state = patient.getAddress().get(0).getState();
        postCode = patient.getAddress().get(0).getPostalCode();
        country = patient.getAddress().get(0).getCountry();
        street = patient.getAddress().get(0).getLine().get(0).toString();
        gender = patient.getGender().toString();
        telephone = patient.getTelecom().get(0).getValue().split(" ")[0];

//        if(patient.getId()==null) patient.setId(patient.getIdentifierFirstRep().getValue());
        if (patient.getId() == null) patient.setId(new IdDt(patient.getIdentifierFirstRep().getValue()));
        this.birthDate = patient.getBirthDate();
        birthDateStr = format.format(birthDate);
        register = new LocalRegister();

        //        System.out.println(patient.getId());
        //        if(patient.getI)
    }

    public LocalPatient() {
        id = "";
        name = "";
        lastName = "";
        birthDate = null;
        birthDateStr = "";
        gender = "";
        telephone = "";
        adress = "";
        state = "";
        city = "";
        postCode = "";
        country = "";
    }

    private String obtainSecondName(Patient patient) {
        String sName;

        try {
//            sName = String.join(" ",
            sName = patient.getName()
                    .get(0)
                    .getFamily();
//                            .stream()
//                            .map(StringDt::getValueNotNull)
//                            .collect(Collectors.toList()));
        } catch (Exception e) {
            sName = "";
            //todo log exception
        }

        return sName.replaceAll("\\d", "");
    }

    private String obtainName(Patient patient) {
        String name;

        try {
            name = String.join(" ",
                    patient.getName()
                            .get(0)
                            .getGiven()
                            .stream()
//                            .map(StringDt::getValueNotNull)
                            .map(StringType::getValueNotNull)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            name = "";
            //todo log exception
        }
        return name.replaceAll("\\d", "");
    }

    public String getListViewFormat() {
        return name + " " + lastName;
    }

    @Override
    public String toString() {
        return getListViewFormat();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public LocalRegister getRegister() {
        return register;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDateStr() {
        return birthDateStr;
    }

    public void setBirthDateStr(String birthDateStr) {
        this.birthDateStr = birthDateStr;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isYoungerOlder(LocalDate value, String str) {
        boolean selected = str.contains("after");
        try {
            Date compareDate = Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
            return selected ? birthDate.after(compareDate) : birthDate.before(compareDate);
        } catch (Exception e) {
            return true;
        }
    }

    public void put(MedicationRequest resource) {
        register.put(resource);
    }

    public void put(Observation resource) {
        register.put(resource);
    }

    public String getHeader() {
        return name + " " + lastName;
    }


}
