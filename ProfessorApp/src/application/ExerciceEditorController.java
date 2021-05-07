package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

public class ExerciceEditorController implements Initializable{

	@FXML
	MediaView importMediaView;
	
	@FXML
	Button importButton;
	
	@FXML
	TextArea instruction;
	
	@FXML
	TextArea help;
	
	@FXML
	TextArea text;
	
	@FXML
	TextField title;
	
	@FXML
	Button parameter;
	
	@FXML
	TextField filePath;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		importButton.setOnAction(ActionEvent -> 
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
				filePath.setText(file.getAbsolutePath());
				try {
					importMediaView.setMediaPlayer(new MediaPlayer(new Media(file.toURI().toURL().toExternalForm())));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
		);
		
	}
	
}
