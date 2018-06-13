package app;

import app.connection.HapiConnection;
//import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.rest.client.api.IGenericClient;
import app.controler.MainWindowController;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Wojciech Jaronski
 */

public class Main extends Application {

    private static AppProperties properties;
    private static HapiConnection hc;
    private static String current_url = "";

    public static AppProperties getProperties(){
        return properties;
    }

    public static HapiConnection getHapiConnection() {
        if(hc == null) hc = new HapiConnection();
        return hc;
    }

    public static HapiConnection getHapiConnection(String url){
        if(url.equals(current_url)) return getHapiConnection();
        else {
            current_url = url;
            return hc = new HapiConnection(current_url);
        }
    }

    public static FhirContext ctx;// = FhirContext.forDstu3();
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/main_window.fxml"));


        Parent root = loader.load();
        MainWindowController controller = loader.getController();
//        controller.setHostServices(getHostServices());

        primaryStage.getProperties().put("hostServices", this.getHostServices());

        primaryStage.setTitle("Elektroniczna karta pacjenta");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(e -> properties.exportSettings());
        primaryStage.show();

    }


    public static void main(String[] args) {
        properties = new AppProperties();
        launch(args);
    }
}
