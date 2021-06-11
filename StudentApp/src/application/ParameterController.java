package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ParameterController implements Initializable {

	@FXML private Text createdExercisePathText;
	@FXML private Text studentExercisePathText;
	@FXML private Button createdExerciseButton;
	@FXML private Button studentExerciseButton;
	
	@FXML private Rectangle slideContainer;
	@FXML private Circle slideButton;
	
	@FXML private Button saveButton;
	
	@FXML private Text problemPath;
	
	private File createdExercisePath;
	private File studentExercsiePath;
	
	private boolean darkModeActivated = false;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {
			load();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		if (createdExercisePath == null || studentExercsiePath == null) {
			problemPath.setVisible(true);
		}else {
			problemPath.setVisible(false);
		}
		
	}

	@FXML
	public void getPathCreatedExercise() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		createdExercisePath = directoryChooser.showDialog(createdExerciseButton.getScene().getWindow());
		createdExercisePathText.setText(createdExercisePath.getAbsolutePath());
		if (studentExercsiePath != null) {
			problemPath.setVisible(false);
		}
	}
	
	@FXML
	public void getPathStudentExercise() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		studentExercsiePath = directoryChooser.showDialog(studentExerciseButton.getScene().getWindow());
		studentExercisePathText.setText(studentExercsiePath.getAbsolutePath());
		if (createdExercisePath != null) {
			problemPath.setVisible(false);
		}
	}
	
	@FXML
	public void slide() {
		if (darkModeActivated) {
			slideContainer.setFill(Color.WHITE);
			slideButton.setTranslateX(0.0);
			darkModeActivated = false;
			unsetDarkMode(Main.getScenes());
		}else {
			slideContainer.setFill(Color.LIMEGREEN);
			slideButton.setTranslateX(21.5);
			darkModeActivated = true;
			setDarkMode(Main.getScenes());
		}
	}
	
	public void setStyleSheet() {
		if (darkModeActivated) {
			setDarkMode(Main.getScenes());
		}else {
			unsetDarkMode(Main.getScenes());
		}
	}
	
	private void setDarkMode(List<Scene> scenes) {
		slideContainer.setFill(Color.LIMEGREEN);
		slideButton.setTranslateX(21.5);
		for (Scene scene : scenes) {
			scene.getStylesheets().clear();
			scene.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());
		}
	}

	private void unsetDarkMode(List<Scene> scenes) {
		slideContainer.setFill(Color.WHITE);
		slideButton.setTranslateX(0.0);
		for (Scene scene : scenes) {
			scene.getStylesheets().clear();
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		}
	}
	
	@FXML
	public void save() throws IOException {
		FileWriter fileWriter = new FileWriter(getClass().getClassLoader().getResource("parameter.txt").getPath().toString());
		fileWriter.write("Created Exercise directory : " + createdExercisePathText.getText());
		fileWriter.write("\n");
		fileWriter.write("Student Exercise directory : " + studentExercisePathText.getText());
		fileWriter.write("\n");
		fileWriter.write("Dark mode : " + darkModeActivated);
		fileWriter.close();
		((Stage) saveButton.getScene().getWindow()).close();
	}
	
	private void load() throws IOException, URISyntaxException {
		File parameter = new File(getClass().getClassLoader().getResource("parameter.txt").getPath().toString());
		BufferedReader reader= new BufferedReader(new FileReader(parameter));
		String string = reader.readLine();
		while (string != null) {
			if (string.contains("Created Exercise directory : ")) {
				int index = "Created Exercise directory : ".length();
				if ("Aucun dossier selectionné".equals(string.substring(index))) {
					createdExercisePath = null;
				}else {
					createdExercisePath = new File(string.substring(index));
				}
				createdExercisePathText.setText(string.substring(index));
			}
			if (string.contains("Student Exercise directory : ")) {
				int index = "Student Exercise directory : ".length();
				if ("Aucun dossier selectionné".equals(string.substring(index))) {
					studentExercsiePath = null;
				}else {
					studentExercsiePath = new File(string.substring(index));
				}
				studentExercisePathText.setText(string.substring(index));
			}
			if (string.contains("Dark mode : ")) {
				int index = "Dark mode : ".length();
				string = string.substring(index);
				if (string.equals("true")) {
					darkModeActivated = true;
				}
			}
			string = reader.readLine();
		}
		reader.close();
	}
	
	public File getCreatedExercisePath() {
		return createdExercisePath;
	}
	
	public File getStudentExercisePath() {
		return studentExercsiePath;
	}
}
