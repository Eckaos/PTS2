package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class ResultScreenController implements Initializable {

	@FXML private Label instruction;
	@FXML private Label averageNumberOfWord;
	@FXML private Label completeText;
	@FXML private Label reconstructedText;
	
	@FXML private MediaView mediaView;
	@FXML private Button playButton;
	@FXML private Button muteButton;
	@FXML private Slider timeSlider;
	@FXML private Slider volumeSlider;
	@FXML private ImageView imageView;
	
	private MediaPlayer mediaPlayer;
	private Media media;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void setFileToModify(File currentFile) {
		// TODO Auto-generated method stub
		
	}

	public void parseExercise(File currentFile) {
		// TODO Auto-generated method stub
		
	}

	
	
}
