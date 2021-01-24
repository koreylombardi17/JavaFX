/* Name: Korey Lombardi
 Course: CNT 4714 – Spring 2021
 Assignment title: Project 1 – Event-driven Enterprise Simulation
 Date: Saturday January 23, 2021
*/
package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene= new Scene(root, 700, 220);
        stage.setTitle("Nile Dot Com - Spring 2021");
        stage.setScene(scene);
        stage.show();

        // Load Inventory and store in a List
        Controller.loadFile();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
