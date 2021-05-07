package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class AccueilController implements Initializable{

	@FXML
	Button createModifyButton;
	
	@FXML
	Button result;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		createModifyButton.setOnAction(ActionEvent -> Main.setScreen(1));
		result.setOnAction(ActionEvent -> Main.setScreen(2));
	}
	
}
