package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

	@FXML MenuItem newExercise;

	@FXML MenuItem seeResults;

	@FXML MenuItem close;

	@FXML MenuItem parameter;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) seeButton.getScene().getWindow();
			stage.close();
		});

		newExercise.setOnAction(ActionEvent -> Main.setScreen(2));
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());

		File exerciseDirectory = null;
		if (Main.getParameterController().getStudentExercisePath() != null) {
			exerciseDirectory = Main.getParameterController().getStudentExercisePath();
			if (exerciseDirectory.exists()) {
				for (File file : exerciseDirectory.listFiles()) {
					if (".student".equals(FileUtil.getExtension(file))) {
						examFiles.getItems().add(FileUtil.stripExtension(file));
					}
				}
			}
		}

		examFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				elevesListView.getItems().clear();
				for (File file : Main.getParameterController().getStudentExercisePath().listFiles()) {
					if (file.getName().contains(examFiles.getSelectionModel().getSelectedItem())) {
						elevesListView.getItems().add(FileUtil.stripExerciceName(file));
					}
					
				}
			}
		});
		
		elevesListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				currentFile = new File(Main.getParameterController().getStudentExercisePath().getAbsolutePath()+"/"+examFiles.getSelectionModel().getSelectedItem()+".student");
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
					// TODO Auto-generated catch block
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

}