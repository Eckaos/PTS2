package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ModifyExerciseController implements Initializable{

	@FXML
	private ListView<String> trainingFiles;
	
	@FXML
	private ListView<String> examFiles;
	
	@FXML
	private Button modifyButton;
	
	@FXML
	private Button Addbutton;
	
	private File currentFile;
	
	@FXML
	MenuItem newExercise;
	
	@FXML
	MenuItem seeResults;
	
	@FXML
	MenuItem close;
	
	@FXML
	MenuItem parameter;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) Addbutton.getScene().getWindow();
			stage.close();
		});
		
		newExercise.setOnAction(ActionEvent -> Main.setScreen(2));
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());
		
		File exerciseDirectory;
		if (Main.getParameterController().getCreatedExercisePath() != null) {
			exerciseDirectory = Main.getParameterController().getCreatedExercisePath();
			ObservableList<String> trainingFileList = FXCollections.observableArrayList();
			ObservableList<String> examFileList = FXCollections.observableArrayList();
			for (File file : exerciseDirectory.listFiles()) {
				if (".train".equals(FileUtil.getExtension(file))) {
					trainingFileList.add(FileUtil.stripExtension(file));
				}
				if (".exam".equals(FileUtil.getExtension(file))) {
					examFileList.add(FileUtil.stripExtension(file));
				}
			}
			trainingFiles.setItems(trainingFileList);
			examFiles.setItems(examFileList);
		}
		
		trainingFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				ExerciceEditorController exerciceEditorController = Main.getExerciseEditorLoader().getController();
				currentFile = new File("C:\\Users\\NathanPollart\\git\\PTS2\\ProfessorApp\\exercise\\training\\"+trainingFiles.getSelectionModel().getSelectedItem()+".bin");
				if (click.getClickCount() == 2) {
					
					try {
						exerciceEditorController.setFileToModify(currentFile);
						exerciceEditorController.parseExercise(currentFile);
						//exerciceEditorController.setFile(new File("C:/Users/NathanPollart/git/PTS2/ProfessorApp/temp.mp4"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					Main.setScreen(2);
				}
				
			}
		});
		examFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				ExerciceEditorController exerciceEditorController = Main.getExerciseEditorLoader().getController();
				currentFile = new File("C:\\Users\\NathanPollart\\git\\PTS2\\ProfessorApp\\exercise\\exam\\"+examFiles.getSelectionModel().getSelectedItem()+".bin");
				if (click.getClickCount() == 2) {
					try {
						exerciceEditorController.setFileToModify(currentFile);
						exerciceEditorController.parseExercise(currentFile);
						//exerciceEditorController.setFile(new File("C:/Users/NathanPollart/git/PTS2/ProfessorApp/temp.mp4"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					Main.setScreen(2);
				}
			}
		});
		
		Addbutton.setOnAction(ActionEvent -> 
		{
			currentFile = null;
			ExerciceEditorController exerciceEditorController = Main.getExerciseEditorLoader().getController();
			exerciceEditorController.setFileToModify(null);
			Main.setScreen(2);
		});
		
		
		modifyButton.setOnAction(ActionEvent -> 
		{
			if (trainingFiles.getSelectionModel().getSelectedItem() == null && examFiles.getSelectionModel().getSelectedItem() == null) {
				return;
			}
			ExerciceEditorController exerciceEditorController = Main.getExerciseEditorLoader().getController();
			exerciceEditorController.setFileToModify(currentFile);
			Main.setScreen(2);
		});
	}

	public File getCurrentFile() {
		if (currentFile == null) {
			return null;
		}
		return currentFile;
	}
}
