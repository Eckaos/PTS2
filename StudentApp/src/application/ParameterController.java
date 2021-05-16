package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

public class ParameterController implements Initializable {

	@FXML private Spinner<Integer> fontSizeSpinner;
	
	@FXML private Text createdExercisePathText;
	@FXML private Text studentExercisePathText;
	@FXML private Button createdExerciseButton;
	@FXML private Button studentExerciseButton;
	
	@FXML private Rectangle slideContainer;
	@FXML private Circle slideButton;
	
	@FXML private Button saveButton;
	
	private File createdExercisePath;
	private File studentExercsiePath;
	
	private boolean darkModeActivated = false;
	private SpinnerValueFactory<Integer> valueFactory;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 30, 10);
		fontSizeSpinner.setValueFactory(valueFactory);
		
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (darkModeActivated) {
			setDarkMode();
		}else {
			unsetDarkMode();
		}
		
		
	}

	@FXML
	public void getPathCreatedExercise() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		createdExercisePath = directoryChooser.showDialog(createdExerciseButton.getScene().getWindow());
		createdExercisePathText.setText(createdExercisePath.getAbsolutePath());
	}
	
	@FXML
	public void getPathStudentExercise() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");
		studentExercsiePath = directoryChooser.showDialog(studentExerciseButton.getScene().getWindow());
		studentExercisePathText.setText(studentExercsiePath.getAbsolutePath());
	}
	
	@FXML
	public void slide() {
		if (darkModeActivated) {
			slideContainer.setFill(Color.WHITE);
			slideButton.setTranslateX(0.0);
			darkModeActivated = false;
			unsetDarkMode();
		}else {
			slideContainer.setFill(Color.LIMEGREEN);
			slideButton.setTranslateX(21.5);
			darkModeActivated = true;
			setDarkMode();
		}
	}
	
	private void setDarkMode() {
		// TODO Auto-generated method stub
		
	}

	private void unsetDarkMode() {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	public void save() throws IOException {
		FileWriter fileWriter = new FileWriter("parameter.txt");
		fileWriter.write("Font size : " + fontSizeSpinner.getValue());
		fileWriter.write("\n");
		fileWriter.write("Created Exercise directory : " + createdExercisePathText.getText());
		fileWriter.write("\n");
		fileWriter.write("Student Exercise directory : " + studentExercisePathText.getText());
		fileWriter.write("\n");
		fileWriter.write("Dark mode : " + darkModeActivated);
		fileWriter.close();
	}
	
	private void load() throws IOException {
		File parameter = new File("parameter.txt");
		BufferedReader reader= new BufferedReader(new FileReader(parameter));
		String string = reader.readLine();
		while (string != null) {
			if (string.contains("Font size : ")) {
				int index = "Font size : ".length();
				int size = Integer.parseInt(string.substring(index));
				valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 30, size);
				fontSizeSpinner.setValueFactory(valueFactory);
				setFontSize();
			}
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

	private void setFontSize() {
		// TODO Auto-generated method stub
		
	}
	
	public int getFontSize() {
		return fontSizeSpinner.getValue();
	}
	
	public String test() {
		return getCreatedExercisePath().getAbsolutePath();
	}
	
	public File getCreatedExercisePath() {
		return createdExercisePath;
	}
	
	public File getStudentExercisePath() {
		return studentExercsiePath;
	}
}
