package app.controler;

import app.Main;
import app.model.LocalPatient;
import app.model.MyPoint;
import app.model.MyValue;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Wojciech Jaronski
 */

public class PatientInfoController implements Initializable {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private LocalPatient patient;

    public Label headerLabel;
    public GridPane infoGrid;
    public TableView<MyValue> tableView;
    public TableColumn<MyValue, String> eventColumn, dateColumn, showDateColumn;
    public DatePicker startDate, endDate;
    public TextField eventFilter;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpTable();
    }

    private void setUpTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        showDateColumn.setCellValueFactory(val -> new SimpleStringProperty(val.getValue().getDateAsString()));
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("string"));

        setConverter(startDate);
        setConverter(endDate);
    }

    static void setConverter(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                datePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    void setPatient(LocalPatient patient) {

        this.patient = patient;
        Main.getHapiConnection().downloadEverythingAboutPatient(patient);


        Map<String, MyPoint> fields = LocalPatient.getFields();

        Arrays.stream(LocalPatient.class.getDeclaredFields())
                .map(Field::getName)
                .forEach(fieldName -> {
                    if (fields.containsKey(fieldName)) {
                        String val = getValueFromField(fieldName);
                        MyPoint point = fields.get(fieldName);
                        Node node = getNode(point.getLabel(), val);
                        infoGrid.add(node, point.getCol(), point.getRow());
                    }
                });

        setItemsForListView();

    }

    private void setItemsForListView() {
        Map<Date, ArrayList<MyValue>> map = patient.getRegister().getRegister();
        ArrayList<MyValue> allValues = new ArrayList<>();

        for (Date date : map.keySet()) {
            ArrayList<MyValue> temp = map.get(date);
            temp.stream().skip(1).forEach(val -> val.setDate2(null));
            allValues.addAll(temp);
        }
        boolean even = false;

        Collections.sort(allValues);

        FilteredList<MyValue> filter = new FilteredList<>(FXCollections.observableArrayList(allValues), s -> true);

        tableView.setItems(filter);


        filter.predicateProperty().bind(Bindings.createObjectBinding(() ->
                        value -> value.getString().toLowerCase().contains(eventFilter.getText().toLowerCase())
                                && value.isAfter(startDate.getValue()) && value.isBefore(endDate.getValue()),
                eventFilter.textProperty(),
                startDate.valueProperty(),
                endDate.valueProperty()
        ));

    }

    private Node getDateLabel(String str, boolean even) {
        Label label = new Label(str);
        if (even) label.setStyle("-fx-background-color: greenyellow");
        else label.setStyle("-fx-background-color: #58a1ff");

        return label;
    }

    private Node getLabel(String str, boolean even) {
        Label label = new Label(str);
        if (even) label.setStyle("-fx-background-color: greenyellow");
        else label.setStyle("-fx-background-color: #58a1ff");
        return label;
    }


    private String getValueFromField(String fieldName) {
        try {
            Object f = new PropertyDescriptor(fieldName, LocalPatient.class).getReadMethod().invoke(patient);

            return (String) f;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    private Node getNode(String header, String value) {
        Label l1 = new Label(header);
        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);

        Label l2 = new Label(value);
        Region r2 = new Region();
        HBox.setHgrow(r2, Priority.ALWAYS);

        return new HBox(l1, r1, l2, r2);
    }
}
