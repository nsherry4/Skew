package net.sciencestudio.skew.javafx.dataset;

import net.sciencestudio.skew.javafx.tabs.TabsController;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DatasetController {

	public TabsController tabs;
	
	@FXML
	public BorderPane box;
	
	@FXML
	public VBox sidebar;
	
	
	public void open() {
		tabs.open();
	}
	
	
}
