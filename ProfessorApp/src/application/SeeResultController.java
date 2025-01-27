package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SeeResultController implements Initializable{


	@FXML private ListView<String> elevesListView;

	@FXML private ListView<String> examFiles;

	@FXML private Button seeButton;
	private File currentFile;

	@FXML private MenuItem reception;
	@FXML private MenuItem newExercise;
	@FXML private MenuItem modifExercise;
	@FXML private MenuItem close;
	@FXML private MenuItem parameter;
	
	@FXML private Button deleteButton;
	
	private List<String> exercisesFullName = new ArrayList<>();

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refreshList();
		deleteButton.setVisible(false);
		deleteButton.setOnAction(ActionEvent -> {
			if (currentFile != null) {
				currentFile.delete();
				elevesListView.getItems().remove(FileUtil.stripExerciceName(currentFile));
			}
		});
		
		reception.setOnAction(ActionEvent -> {
			Main.setScreen(0);
		});
		
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) seeButton.getScene().getWindow();
			stage.close();
		});

		newExercise.setOnAction(ActionEvent -> {
			Main.getExerciceEditorController().reset();
			Main.setScreen(2);
		});
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());
		modifExercise.setOnAction(ActionEvent -> {
			if (Main.getParameterController().getCreatedExercisePath() != null) {
				Main.setScreen(1);
				Main.getModifyExerciseController().refreshList();
			}
		});

		examFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				elevesListView.getItems().clear();
				exercisesFullName.clear();
				for (File file : Main.getParameterController().getStudentExercisePath().listFiles()) {
					if (FileUtil.getExerciceName(file).contains(examFiles.getSelectionModel().getSelectedItem()) && FileUtil.getExtension(file).equals(".student")) {
						elevesListView.getItems().add(FileUtil.stripExerciceName(file));
						exercisesFullName.add(file.getName());
					}
					
				}
			}
		});
		
		elevesListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				currentFile = new File(Main.getParameterController().getStudentExercisePath().getAbsolutePath()+"/"+exercisesFullName.get(elevesListView.getSelectionModel().getSelectedIndex()));
				deleteButton.setVisible(true);
				if (click.getClickCount() == 2) {
					try {
						Main.getResultScreenController().parseExercise(currentFile);
						Main.setScreen(4);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});


		seeButton.setOnAction(ActionEvent -> 
		{
			if (currentFile != null) {
				try {
					Main.getResultScreenController().parseExercise(currentFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Main.setScreen(4);
			}
		});
	}
	
	public File getCurrentFile() {
		if (currentFile == null) {
			return null;
		}
		return currentFile;
	}

	public void refreshList() {
		File exerciseDirectory = null;
		examFiles.getItems().clear();
		if (Main.getParameterController().getStudentExercisePath() != null) {
			exerciseDirectory = Main.getParameterController().getStudentExercisePath();
			if (exerciseDirectory.exists()) {
				for (File file : exerciseDirectory.listFiles()) {
					if (".exam".equals(FileUtil.getExtension(file))) {
						examFiles.getItems().add(FileUtil.stripExtension(file));
					}
				}
			}
		}
	}
}
