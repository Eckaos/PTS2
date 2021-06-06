package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ModifyExerciseController implements Initializable{

	@FXML private ListView<String> trainingFiles;
	
	@FXML private ListView<String> examFiles;
	
	@FXML private Button modifyButton;
	
	@FXML private Button Addbutton;
	
	private File currentFile;
	
	@FXML private MenuItem newExercise;
	
	@FXML private MenuItem seeResults;
	
	@FXML private MenuItem close;
	
	@FXML private MenuItem parameter;
	
	@FXML private Button deleteButton;
	
	private Map<File, ListView<String>> fileMap = new HashMap<>();
	
	//TODO reinitialize exercice Editor on new exercise
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		deleteButton.setOnAction(ActionEvent -> {
			if (currentFile != null) {
				currentFile.delete();
				fileMap.get(currentFile).getItems().remove(FileUtil.stripExtension(currentFile));
			}
		});
		
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) Addbutton.getScene().getWindow();
			stage.close();
		});
		
		newExercise.setOnAction(ActionEvent -> {
			Main.getExerciceEditorController().reset();
			Main.setScreen(2);
		});
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());
		
		File exerciseDirectory;
		if (Main.getParameterController().getCreatedExercisePath() != null) {
			exerciseDirectory = Main.getParameterController().getCreatedExercisePath();
			if (exerciseDirectory.exists()) {
				for (File file : exerciseDirectory.listFiles()) {
					if (".train".equals(FileUtil.getExtension(file))) {
						fileMap.put(file, trainingFiles);
						trainingFiles.getItems().add(FileUtil.stripExtension(file));
					}
					if (".exam".equals(FileUtil.getExtension(file))) {
						fileMap.put(file, examFiles);
						examFiles.getItems().add(FileUtil.stripExtension(file));
					}
				}
			}
		}
		
		trainingFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				currentFile = new File(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+trainingFiles.getSelectionModel().getSelectedItem()+".train");
				examFiles.getSelectionModel().select(null);
				if (click.getClickCount() == 2) {
					try {
						Main.getExerciceEditorController().setFileToModify(currentFile);
						Main.getExerciceEditorController().parseExercise(currentFile);
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
				currentFile = new File(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+examFiles.getSelectionModel().getSelectedItem()+".exam");
				trainingFiles.getSelectionModel().select(null);
				if (click.getClickCount() == 2) {
					try {
						Main.getExerciceEditorController().setFileToModify(currentFile);
						Main.getExerciceEditorController().parseExercise(currentFile);
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
			Main.getExerciceEditorController().setFileToModify(null);
			Main.getExerciceEditorController().reset();
			Main.setScreen(2);
		});
		
		
		modifyButton.setOnAction(ActionEvent -> 
		{
			if (trainingFiles.getSelectionModel().getSelectedItem() == null && examFiles.getSelectionModel().getSelectedItem() == null) {
				return;
			}
			try {
				Main.getExerciceEditorController().parseExercise(currentFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Main.getExerciceEditorController().setFileToModify(currentFile);
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
