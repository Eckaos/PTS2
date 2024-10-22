package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModifyExerciseController implements Initializable{

	@FXML private ListView<String> trainingFiles;
	@FXML private ListView<String> examFiles;
	@FXML private Button modifyButton;
	@FXML private Button Addbutton;
	private File currentFile;
	@FXML private MenuItem reception;
	@FXML private MenuItem newExercise;
	@FXML private MenuItem seeResults;
	@FXML private MenuItem close;
	@FXML private MenuItem parameter;
	@FXML private Button deleteButton;
	private Map<File, ListView<String>> fileMap = new HashMap<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("DeletePopUp.fxml"));
		Stage stageDelete = new Stage();
		stageDelete.initModality(Modality.APPLICATION_MODAL);
		stageDelete.setAlwaysOnTop(true);
		stageDelete.setResizable(false);
		Scene scene = null;
		try {
			scene = new Scene(loader.load());
			stageDelete.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DeletePopUpController controller = loader.getController();
		Main.getScenes().add(scene);
		reception.setOnAction(ActionEvent -> {
			Main.setScreen(0);
		});
		deleteButton.setOnAction(ActionEvent -> {
			if (currentFile != null) {
				controller.setFile(currentFile);
				stageDelete.show();
				refreshList();
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
		
		trainingFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (Main.getParameterController().getCreatedExercisePath() == null) {
					return;
				}
				currentFile = new File(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+trainingFiles.getSelectionModel().getSelectedItem()+".train");
				examFiles.getSelectionModel().select(null);
				if (click.getClickCount() == 2) {
					try {
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
				if (Main.getParameterController().getCreatedExercisePath() == null) {
					return;
				}
				currentFile = new File(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+examFiles.getSelectionModel().getSelectedItem()+".exam");
				trainingFiles.getSelectionModel().select(null);
				if (click.getClickCount() == 2) {
					try {
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
			Main.setScreen(2);
		});
	}

	public File getCurrentFile() {
		if (currentFile == null) {
			return null;
		}
		return currentFile;
	}
	
	public void refreshList() {
		File exerciseDirectory;
		trainingFiles.getItems().clear();
		examFiles.getItems().clear();
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
	}
}
