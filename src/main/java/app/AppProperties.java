package app;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Wojciech Jaronski
 */

public class AppProperties implements Serializable {
    private static final String URL_KEY = "baseUrl";
    private static final String DEFAULT_URL = "http://localhost/";
    private static final String FILE_NAME = "app.properties";
    private String url = DEFAULT_URL;

    public AppProperties(){
        importSettings();
    }

    private void importSettings() {
        try {
            File file = new File(FILE_NAME);
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            url = properties.getProperty(URL_KEY, DEFAULT_URL);

//            Enumeration enuKeys = properties.keys();
//            while (enuKeys.hasMoreElements()) {
//                String key = (String) enuKeys.nextElement();
//                String value = properties.getProperty(key);
//
//                System.out.println(key + ": " + value);
//            }
        } catch (FileNotFoundException e) {
            url = DEFAULT_URL;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportSettings() {
        try {
            Properties properties = new Properties();
            properties.setProperty(URL_KEY, url);
//            properties.setProperty("favoriteContinent", "Antarctica");
//            properties.setProperty("favoritePerson", "Nicole");

            File file = new File(FILE_NAME);
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Configuration for app");
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
