package application;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;

public class ExerciceController implements Initializable{

	@FXML
	MediaView testMe;
	@FXML
	Text instructionText;
	@FXML
	Button pausePlayButton;
	@FXML
	Slider progressBar;
	@FXML
	Button soundButton;
	
	
	@FXML
	Text timeText;
	@FXML
	Button helpButton;
	@FXML
	Text textToFind;
	@FXML
	TextField typedText;
	@FXML
	Button validateButton;
	@FXML
	Button finishButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File pathString = new File("src/vagos.mp4");
		
		try {
			testMe.setMediaPlayer(new MediaPlayer(new Media(pathString.toURI().toURL().toExternalForm())));
			testMe.getMediaPlayer().setAutoPlay(true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		pausePlayButton.setOnAction(ActionEvent -> 
			{
				if (testMe.getMediaPlayer().getStatus().equals(Status.PAUSED)) {
					testMe.getMediaPlayer().play();
				}else {
					testMe.getMediaPlayer().pause();
				}
			}
		);
		
		soundButton.setOnMousePressed(ActionEvent -> 
		{
			if (testMe.getMediaPlayer().isMute()) {
				testMe.getMediaPlayer().setMute(false);
			}else {
				testMe.getMediaPlayer().setMute(true);
			}
			
		}
		);
		
		testMe.setOnMouseClicked(ActionEvent -> 
			{
				if (testMe.getMediaPlayer().getStatus().equals(Status.PAUSED)) {
					testMe.getMediaPlayer().play();
				}else {
					testMe.getMediaPlayer().pause();
				}
			}
		);
		
		//TODO recuperer le text depuis l'exercice
		String text = null;
		textToFind.setText(text);
		
		validateButton.setOnAction(ActionEvent -> verify(typedText.getText()));
		
		//TODO faire la popup
		finishButton.setOnAction(null);
	}

	private void verify(String text) {
		// TODO faire l'algorithme de découverte de mot
	}
}
