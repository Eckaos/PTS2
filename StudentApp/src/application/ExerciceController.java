package application;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.MediaView;
import javafx.scene.media.*;

public class ExerciceController implements Initializable{

	@FXML
	MediaView testMe;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File pathString = new File("src/vagos.mp4");
		
		try {
			testMe.setMediaPlayer(new MediaPlayer(new Media(pathString.toURI().toURL().toExternalForm())));
			testMe.getMediaPlayer().setAutoPlay(true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
	}
}
