package app.controler;

import app.Main;
import app.connection.HapiConnection;
import app.model.LocalPatient;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hl7.fhir.dstu3.model.Patient;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Wojciech Jaronski
 */

public class MainWindowController implements Initializable {
    public static boolean downloadComplete = false;
    private static final String LIST_OF_TEST_SERVERS = "http://wiki.hl7.org/index.php?title=Publicly_Available_FHIR_Servers_for_testing";
    private Stage stage;

    //    @Inject
    private HostServices hostServices;
    private List<LocalPatient> patients;

    //FXML declarations
//    public ListView<LocalPatient> patientsListView;
    public Button updateButton;
    public Hyperlink listHyperlink;
    public TableView<LocalPatient> patientsTable;
    public TableColumn<LocalPatient, String> firstNameColumn, lastNameColumn, birthDateColumn;
    public TextField urlTextField, nameFilter, lastNameFilter;
    public Button youngerOlderFilter;
    public DatePicker birthDateFilter;
    public CheckBox dateFilterEnabled;


    private HapiConnection hc;

    public void update() {

        downloadComplete = false;
        Thread td = new Thread(() -> {

            while (MainWindowController.downloadComplete) {

            }
        });
        td.start();

        Main.getProperties().setUrl(urlTextField.getText());
        hc = Main.getHapiConnection(urlTextField.getText());
        setItemsForListView();
        downloadComplete = true;
    }


    public void initialize(URL location, ResourceBundle resources) {
        urlTextField.setText(Main.getProperties().getUrl());
        hc = Main.getHapiConnection(urlTextField.getText());

        setItemsForListView();
        setUp();
        setUpTable();
    }

    private void setUpTable() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDateStr"));

        patientsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/patient_info.fxml"));
                    Parent root = loader.load();
                    ((PatientInfoController) loader.getController()).setPatient(patientsTable.getSelectionModel().getSelectedItem());

                    Stage newStage = new Stage();
                    newStage.setScene(new Scene(root));
                    newStage.initModality(Modality.APPLICATION_MODAL);
                    newStage.showAndWait();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setItemsForListView() {
        Platform.runLater(() -> {
            FilteredList<LocalPatient> filteredList = new FilteredList<>(
                    FXCollections.observableArrayList(patients = hc.getAllPatients()),
                    s -> true);

            filteredList.predicateProperty().bind(Bindings.createObjectBinding(() ->
                            patient -> patient.getName().toLowerCase().contains(nameFilter.getText().toLowerCase())
                                    && patient.getLastName().toLowerCase().contains(lastNameFilter.getText().toLowerCase())
                                    && (!dateFilterEnabled.isSelected() ||
                                    (dateFilterEnabled.isSelected() && patient.isYoungerOlder(birthDateFilter.getValue(), youngerOlderFilter.getText()))),
                    nameFilter.textProperty(),
                    lastNameFilter.textProperty(),
                    dateFilterEnabled.selectedProperty(),
                    birthDateFilter.valueProperty(),
                    youngerOlderFilter.armedProperty()
                    //todo czy starszy mlodszy
            ));


            patientsTable.setItems(filteredList);

        });
    }

    private void setUp() {

        birthDateFilter.setDisable(true);
        PatientInfoController.setConverter(birthDateFilter);
        youngerOlderFilter.setDisable(true);


        dateFilterEnabled.setOnAction(event -> {
            birthDateFilter.setDisable(!dateFilterEnabled.isSelected());
            youngerOlderFilter.setDisable(!dateFilterEnabled.isSelected());
        });

        youngerOlderFilter.setText("Born before");
        youngerOlderFilter.setOnAction(event -> {
            if (youngerOlderFilter.getText().contains("before")) {
                youngerOlderFilter.setText("Born after");
            } else {
                youngerOlderFilter.setText("Born before");
            }
        });

        listHyperlink.setOnMouseClicked(event -> {
            if (stage == null) {
                hostServices = (HostServices) this.getStage().getProperties().get("hostServices");
            }
            hostServices.showDocument(LIST_OF_TEST_SERVERS);
        });
    }

    private Stage getStage() {
        if (this.stage == null)
            this.stage = (Stage) this.patientsTable.getScene().getWindow();
        return stage;
    }

}
