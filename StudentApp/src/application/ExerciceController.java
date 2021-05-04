package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.*;

public class ExerciceController implements Initializable{

	@FXML
	MediaView testMe;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		String pathString = new File("src/Avatar.avi").getAbsolutePath();
		testMe.setMediaPlayer(new MediaPlayer(new Media("/src/Avatar.avi")));
	}

	
}
