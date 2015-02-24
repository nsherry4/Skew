package net.sciencestudio.skew.javafx;

import java.io.IOException;

import net.sciencestudio.skew.javafx.tabs.TabsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

public class SkewFX extends Application {

	public TabsController tabsController;
	
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TabsController.class.getResource("Tabs.fxml"));
        Parent node = loader.load();
        tabsController = loader.getController();
        tabsController.initialize(primaryStage);       
        
        primaryStage.setScene(new Scene(node));
        primaryStage.sizeToScene();
        primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		System.setProperty("prism.lcdtext", "false");
		launch(args);
	}
	
}
