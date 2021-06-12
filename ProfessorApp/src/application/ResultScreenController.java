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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
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

	@FXML private MenuItem reception;
	@FXML private MenuItem newExercise;
	@FXML private MenuItem modifExercise;
	@FXML private MenuItem seeResults;
	@FXML private MenuItem close;
	@FXML private MenuItem parameter;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		reception.setOnAction(ActionEvent -> {
			Main.setScreen(0);
		});
		
		newExercise.setOnAction(ActionEvent -> {
			Main.getExerciceEditorController().reset();
			Main.setScreen(2);
		});
		parameter.setOnAction(ActionEvent -> Main.getParameterStage().show());
		seeResults.setOnAction(ActionEvent -> Main.setScreen(3));
		modifExercise.setOnAction(ActionEvent -> {
			if (Main.getParameterController().getCreatedExercisePath() != null) {
				Main.setScreen(1);
				Main.getModifyExerciseController().refreshList();
			}
		});
		close.setOnAction(ActionEvent -> {
			Stage stage = (Stage) mediaView.getScene().getWindow();
			stage.close();
		});
	}

	public void parseExercise(File currentFile) throws IOException {
		int length;

		String clearText;
		String encryptedText;
		String instructionText;
		boolean mediaType;

		FileInputStream fileInputStream = new FileInputStream(currentFile);

		mediaType = ByteBuffer.wrap(fileInputStream.readNBytes(4)).getInt() == 1 ? true:false;

		length = ByteBuffer.wrap(fileInputStream.readNBytes(4)).getInt();
		clearText = convertByteToString(fileInputStream.readNBytes(length));

		length = ByteBuffer.wrap(fileInputStream.readNBytes(4)).getInt();
		encryptedText = convertByteToString(fileInputStream.readNBytes(length));

		length = ByteBuffer.wrap(fileInputStream.readNBytes(4)).getInt();
		instructionText = convertByteToString(fileInputStream.readNBytes(length));

		length = ByteBuffer.wrap(fileInputStream.readNBytes(8)).getInt();

		FileOutputStream mediaOutputStream;
		FileOutputStream image;

		if (mediaType) {
			mediaOutputStream = new FileOutputStream("temp.mp4");
		}else {
			mediaOutputStream = new FileOutputStream("temp.mp3");
		}

		mediaOutputStream.write(fileInputStream.readNBytes(length));

		length = ByteBuffer.wrap(fileInputStream.readNBytes(8)).getInt();

		if (length > 0) {
			image = new FileOutputStream("temp.png");
			image.write(fileInputStream.readNBytes(length));
			image.close();
		}

		instruction.setText(instructionText);
		completeText.setText(clearText);
		reconstructedText.setText(encryptedText);

		File mediaFile;
		if (mediaType) {
			mediaFile = new File("temp.mp4");
			media = new Media(mediaFile.toURI().toString());
		}else {
			mediaFile = new File("temp.mp3");
			media = new Media(mediaFile.toURI().toString());
		}
		mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);
		setMediaListener(media);
		String[] words = clearText.split("[ \\t\\n\\x0B\\f\\r]");
		String[] foundWords = encryptedText.split("[ \\t\\n\\x0B\\f\\r]");
		int numberWord = words.length;
		int numberFoundWord = 0;
		for (String string : foundWords) {
			if (!string.contains("#")) {
				numberFoundWord++;
			}
		}
		averageNumberOfWord.setText(numberFoundWord +" mots trouvés/" + numberWord);
		fileInputStream.close();
		mediaOutputStream.close();
	}

	private String convertByteToString(byte[] readNBytes) {
		String buildString ="";
		for (int i = 0; i < readNBytes.length; i++) {
			buildString += (char) readNBytes[i];
		}
		return buildString;
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
