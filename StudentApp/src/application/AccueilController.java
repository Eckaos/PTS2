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

public class AccueilController implements Initializable {

	@FXML private Button launchExButton;
	@FXML private ListView<String> trainingFiles;
	@FXML private ListView<String> examFiles;

	@FXML private MenuItem closeItem;
	@FXML private MenuItem parameterItem;
	
	private String selectedEx;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		closeItem.setOnAction(ActionEvent -> ((Stage) launchExButton.getScene().getWindow()).close());
		parameterItem.setOnAction(ActionEvent -> Main.getParameterStage().show());
		
		File exerciseDirectory;
		if (Main.getParameterController().getCreatedExercisePath() != null) {
			exerciseDirectory = Main.getParameterController().getCreatedExercisePath();
			if (exerciseDirectory.exists()) {
				for (File file : exerciseDirectory.listFiles()) {
					if (".train".equals(FileUtil.getExtension(file))) {
						trainingFiles.getItems().add(FileUtil.stripExtension(file));
					}
					if (".exam".equals(FileUtil.getExtension(file))) {
						examFiles.getItems().add(FileUtil.stripExtension(file));
					}
				}
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
				if (trainingFiles.getSelectionModel().getSelectedItem() == null) {
					return;
				}
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
				if (examFiles.getSelectionModel().getSelectedItem() == null) {
					return;
				}
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
