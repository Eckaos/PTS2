package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DeletePopUpController implements Initializable{
	private File file;
	@FXML private Button yesButton;
	@FXML private Button noButton;
	@FXML private Label exerciseName;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML
	private void yes() {
		file.delete();
		Main.getModifyExerciseController().refreshList();
		((Stage) yesButton.getScene().getWindow()).close();
	}
	
	@FXML
	private void no() {
		((Stage) noButton.getScene().getWindow()).close();
	}
	
	public void setFile(File file) {
		this.file = file;
		exerciseName.setText("Voulez-vous vraiment supprimer cet exercice "+file.getName()+" ?");
	}
}
