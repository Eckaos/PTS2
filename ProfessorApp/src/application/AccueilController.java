package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AccueilController implements Initializable{

	@FXML private Button modifyButton;
	@FXML private Button result;
	@FXML private Button addButton;
	@FXML private MenuItem newExercise;
	@FXML private MenuItem modifExercise;
	@FXML private MenuItem seeResults;
	@FXML private MenuItem close;
	@FXML private MenuItem parameter;
	
	//TODO Check seeResult list affichge
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addButton.setOnAction(ActionEvent -> Main.setScreen(2));
		modifyButton.setOnAction(ActionEvent -> {
			Main.setScreen(1);
			Main.getModifyExerciseController().refreshList();
		});
		if (Main.getParameterController().getCreatedExercisePath() == null) {
			modifyButton.setDisable(true);
			addButton.setDisable(true);
			modifExercise.setDisable(true);
			newExercise.setDisable(true);
		}
		
		if (Main.getParameterController().getStudentExercisePath() == null) {
			result.setDisable(true);
			seeResults.setDisable(true);
		}
		seeResults.setOnAction(ActionEvent -> {
			Main.setScreen(3);
			Main.getSeeResultController().refreshList();
		});
		result.setOnAction(ActionEvent -> Main.setScreen(3));
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) addButton.getScene().getWindow();
			stage.close();
		});
		
		seeResults.setOnAction(ActionEvent -> {
			Main.setScreen(3);
			Main.getSeeResultController().refreshList();
		});
		
		newExercise.setOnAction(ActionEvent -> {
			Main.getExerciceEditorController().reset();
			Main.setScreen(2);
		});
		modifExercise.setOnAction(ActionEvent -> 
		{
			if (Main.getParameterController().getCreatedExercisePath() != null) {
				Main.setScreen(1);
				Main.getModifyExerciseController().refreshList();
			}
		});
		
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());
		if (Main.getParameterController().getCreatedExercisePath() == null || Main.getParameterController().getStudentExercisePath() == null) {
			Main.getParameterStage().show();
		}
	}
	
	public Button getAddButton() {
		return addButton;
	}
	
	public MenuItem getModifExercise() {
		return modifExercise;
	}
	
	public Button getModifyButton() {
		return modifyButton;
	}
	
	public MenuItem getNewExercise() {
		return newExercise;
	}
	
	public MenuItem getSeeResults() {
		return seeResults;
	}
	
	public Button getResult() {
		return result;
	}
}
