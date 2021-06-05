package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class PopupController implements Initializable {
	
	@FXML
	private Button non;
	@FXML
	private Button oui;
	@FXML
	private TextField firstNameField;
	@FXML
	private TextField lastNameField;
	@FXML
	private Label firstNameLabel;
	@FXML
	private Label lastNameLabel;

	private ExerciceController soluceController = Main.getExController();
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		firstNameField.setOnKeyTyped(ActionEvent -> {
			if (!firstNameField.getText().equals("") && !lastNameField.getText().equals("")) {
				oui.setDisable(false);
			}
		});
		
		lastNameField.setOnKeyTyped(ActionEvent -> {
			if (!firstNameField.getText().equals("") && !lastNameField.getText().equals("")) {
				oui.setDisable(false);
			}
		});
		
		non.setOnAction(ActionEvent ->{
			((Stage) non.getScene().getWindow()).close();
		});
		
		oui.setOnAction(ActionEvent ->{
			soluceController.setSoluce();
			soluceController.setFileName(firstNameField.getText()+lastNameField.getText());
			if (soluceController.isExam()) {
				try {
					soluceController.saveExercise();
					Main.setScreen(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			((Stage) oui.getScene().getWindow()).close();
		});
	}

	public void reset() {
		if (soluceController.isExam()) {
			oui.setDisable(true);
			firstNameField.setVisible(true);
			lastNameField.setVisible(true);
			firstNameLabel.setVisible(true);
			lastNameLabel.setVisible(true);
		}else {
			oui.setDisable(false);
			firstNameField.setVisible(false);
			lastNameField.setVisible(false);
			firstNameLabel.setVisible(false);
			lastNameLabel.setVisible(false);
		}
		firstNameField.setText("");
		lastNameField.setText("");
	}
}