/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworld;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author navin
 */
public class alertBox {
    
    static Stage window ;
    static Scene scene ;
            
    public static void displayAbout(){
       window = new Stage() ;
       window.initModality(Modality.APPLICATION_MODAL);
       window.setTitle("About Us");
       window.setMinWidth(100);
       
       Label header = new Label("Nice To Meet You");
       Label disc = new Label("We are students at IOE pulchowk Campus");
       Label list_title = new Label("Our Team");
       Label list = new Label("Najma Mathema \n Navin Ayer \n Nishan Patha \n Neeraj Adhikari");
       Button backButton = new Button("Back") ;
       backButton.setOnAction(e -> window.close());
       
       VBox layout = new VBox(10) ;
       layout.getChildren().addAll(header, disc , list_title , list,backButton) ;
       layout.setAlignment(Pos.CENTER);
       
       scene = new Scene(layout,300,300);
       window.setScene(scene);
       window.showAndWait();
       
       
    }
}