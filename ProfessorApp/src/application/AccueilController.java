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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addButton.setOnAction(ActionEvent -> Main.setScreen(2));
		modifyButton.setOnAction(ActionEvent -> Main.setScreen(1));
		if (Main.getParameterController().getCreatedExercisePath() == null) {
			modifyButton.setDisable(true);
		}
		
		result.setOnAction(ActionEvent -> Main.setScreen(3)); //TODO faire result screen et mettre le setOnAction du menuItem
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) addButton.getScene().getWindow();
			stage.close();
		});
		
		seeResults.setOnAction(ActionEvent -> Main.setScreen(3));
		
		newExercise.setOnAction(ActionEvent -> Main.setScreen(2));
		modifExercise.setOnAction(ActionEvent -> 
		{
			if (Main.getParameterController().getCreatedExercisePath() != null) {
				Main.setScreen(1);
			}
		});
		
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());
		
		if (Main.getParameterController().getCreatedExercisePath() == null || Main.getParameterController().getStudentExercisePath() == null) {
			Main.getParameterStage().show();
		}
	}
		
}
