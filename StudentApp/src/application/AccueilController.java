package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class AccueilController implements Initializable {

	@FXML private Button launchExButton;
	@FXML private ListView<String> trainingFiles;
	@FXML private ListView<String> examFiles;

	private String selectedEx;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		File exerciseDirectory;
		if (Main.getParameterController().getCreatedExercisePath() != null) {
			exerciseDirectory = Main.getParameterController().getCreatedExercisePath();
			if (exerciseDirectory.exists()) {
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
		}

		launchExButton.setOnAction(ActionEvent -> {
			if(trainingFiles.getSelectionModel().getSelectedItem() != null || examFiles.getSelectionModel().getSelectedItem() != null) {
				try {
					Main.getExController().setExercise(selectedEx);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Main.setScreen(1);
			}
		});

		trainingFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				selectedEx = trainingFiles.getSelectionModel().getSelectedItem() + ".train";
				if (click.getClickCount() == 2) {
					try {
						Main.getExController().setExercise(selectedEx);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Main.setScreen(1);
				}
			}
		});
		
		examFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				selectedEx = examFiles.getSelectionModel().getSelectedItem() + ".exam";
				if (click.getClickCount() == 2) {
					try {
						Main.getExController().setExercise(selectedEx);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Main.setScreen(1);
				}
			}
		});

	}

}
