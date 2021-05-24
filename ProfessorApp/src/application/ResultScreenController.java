package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

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
	
	@FXML private ImageView playPauseImage;
	@FXML private ImageView muteImage;
	
	private MediaPlayer mediaPlayer;
	private Media media;
	
	private File fileToRead;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void parseExercise(File currentFile) throws IOException {
		int nbBytesToRead;
		String textString;
		String instructionString;
		char occultationChar;
		String occultation;
		byte[] parameter;
		
		//TODO recuperer le texte reconstitue par l'etudiant
		FileInputStream fin = new FileInputStream(currentFile);

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		textString = convertByteToString(fin.readNBytes(nbBytesToRead));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		fin.readNBytes(nbBytesToRead);

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		instructionString = convertByteToString(fin.readNBytes(nbBytesToRead));

		parameter = fin.readNBytes(1);
		occultation = convertByteToString(fin.readNBytes(1));
		occultationChar = occultation.charAt(0);

		fin.readNBytes(4);
		fin.readNBytes(4);

		FileOutputStream fos = null;
		FileOutputStream fos2 = null;
		if (getBit(parameter[0], 6) == 1) {
			fos = new FileOutputStream("temp.mp4");
		} else {
			fos = new FileOutputStream("temp.mp3");
			fos2 = new FileOutputStream("temp.png");
		}
		int bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
		fos.write(fin.readNBytes(bytesRead));

		
		File tempFile = null;
		if (getBit(parameter[0], 6) == 0) {
			bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
			fos2.write(fin.readNBytes(bytesRead));
			imageView.setImage(new Image("file:temp.png"));
			tempFile = new File("temp.mp3");
		}else {
			tempFile = new File("temp.mp4");
		}
		fos.close();
		fin.close();
		media = new Media(tempFile.toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);
		setMediaListener(media);
	}

	private String convertByteToString(byte[] readNBytes) {
		String buildString ="";
		for (int i = 0; i < readNBytes.length; i++) {
			buildString += (char) readNBytes[i];
		}
		return buildString;
	}
	
	private int getBit(byte b, int pos) {
		return (b >> pos) & 1;
	}
	
	private void setMediaListener(Media media) {
		volumeSlider.setValue(mediaPlayer.getVolume()*100);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				mediaPlayer.setVolume(volumeSlider.getValue()/100);
			}
		});

		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				timeSlider.setValue(newValue.toSeconds());
			}
		});

		timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
			}
		});

		timeSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
			}
		});

		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				Duration total = media.getDuration();
				timeSlider.setMax(total.toSeconds());
			}
		});
	}
	
	
	@FXML
	public void playPauseHandle() {
		if (mediaView.getMediaPlayer()== null) {
			return;
		}
		if (mediaView.getMediaPlayer().getStatus().equals(Status.PAUSED) || mediaView.getMediaPlayer().getStatus().equals(Status.READY)) {
			mediaView.getMediaPlayer().play();
			changePlayImage();
		}else {
			mediaView.getMediaPlayer().pause();
			changePlayImage();
		}
	}

	@FXML
	public void muteHandle() {
		if (mediaView.getMediaPlayer()== null) {
			return;
		}
		if(mediaView.getMediaPlayer().isMute()) {
			mediaView.getMediaPlayer().setMute(false);
			changeSpeakerImage();
		}else {
			mediaView.getMediaPlayer().setMute(true);
			changeSpeakerImage();
		}
	}
	
	private void changePlayImage() {
		if (mediaView.getMediaPlayer().getStatus().equals(Status.PAUSED) || mediaView.getMediaPlayer().getStatus().equals(Status.READY)) {
			File tempFile = new File("image/pauseButton.png");
			Image imageTemp = new Image(tempFile.toURI().toString()); 
			playPauseImage.setImage(imageTemp);
		}else {
			File tempFile = new File("image/playButton.png");
			Image imageTemp = new Image(tempFile.toURI().toString()); 
			playPauseImage.setImage(imageTemp);
		}
	}
	
	private void changeSpeakerImage() {
		if(mediaView.getMediaPlayer().isMute()) {
			File tempFile = new File("image/speakerMute.png");
			Image imageTemp = new Image(tempFile.toURI().toString()); 
			muteImage.setImage(imageTemp);
		}else {
			File tempFile = new File("image/speaker.png");
			Image imageTemp = new Image(tempFile.toURI().toString()); 
			muteImage.setImage(imageTemp);
		}
	}
}
