package net.sciencestudio.skew.javafx.tabs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sciencestudio.skew.javafx.dataset.DatasetController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TabsController {

	@FXML
	private TabPane tabs;
	private Stage stage;
	public Tab defaultTab;
	
	public TabsController() throws IOException {
	}
	
	public void initialize(Stage stage) throws IOException {		
		this.stage = stage;
		
        defaultTab = newTab();
        defaultTab.setText("No Data Loaded");
		
		tabs.getSelectionModel().selectedItemProperty().addListener(change -> {
			if (tabs.getTabs().size() == 0) {
				try {
					defaultTab = defaultTab();
				} catch (Exception e) {
				}
			}
			stage.setTitle(tabs.getSelectionModel().getSelectedItem().getText());
		});
	}
	
	public void open() {
		
		FileChooser chooser = new FileChooser();
		List<File> files = chooser.showOpenMultipleDialog(stage);
		
	}
	
	public Tab defaultTab() throws IOException {
        Tab tab = newTab();
        tab.setText("No Data Loaded");
        return tab;
	}
	
	public Tab newTab() throws IOException {
		
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DatasetController.class.getResource("Dataset.fxml"));
        Node node = loader.load();
        
        DatasetController controller = loader.getController();
		controller.tabs = this;
        
        Tab tab = new Tab();
        tab.setClosable(true);
        tab.setContent(node);
        
        tabs.getTabs().add(tab);
        return tab;
        
	}
	
}
