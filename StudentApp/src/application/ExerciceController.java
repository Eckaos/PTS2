package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;

public class ExerciceController implements Initializable {

	@FXML
	private MediaView mediaView;
	@FXML
	private Label instructionText;
	@FXML
	private Button pausePlayButton;
	@FXML
	private Slider progressBar;
	@FXML
	private Button soundButton;

	@FXML
	private Label timeText;
	@FXML
	private Button helpButton;
	@FXML
	private Label textToFind; //TODO Change name
	@FXML
	private TextField typedText;
	@FXML
	private Button validateButton;
	@FXML
	private Button finishButton;

	@FXML
	private Button launchExButton;

	@FXML
	private Slider soundSlider;

	@FXML
	private ImageView imageView;

	private String encryptedText;
	private String helpText;

	private boolean helpDisplayed = false;

	private String fileName;

	private FXMLLoader popupLoader;
	private Stage popupStage;
	private Scene popupScene;

	@FXML private Label realTime;

	//TODO demander le nom d'un etudiant sur la validation de l'exercice exam

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		realTime.setVisible(false);
		popupLoader = new FXMLLoader(getClass().getResource("FinishPopupEx.fxml"));
		BorderPane popupScreen;
		popupStage = new Stage();
		try {
			popupScreen = (BorderPane) popupLoader.load();
			popupScene = new Scene(popupScreen);
			popupStage.setResizable(false);
			popupStage.setScene(popupScene);
			popupStage.initModality(Modality.APPLICATION_MODAL);
		} catch (IOException e) {
			e.printStackTrace();
		}


		helpButton.setOnAction(ActionEvent -> {
			if (helpDisplayed) {
				helpDisplayed = false;
				textToFind.setText(encryptedText);
				typedText.setDisable(false);
				validateButton.setDisable(false);

			}else {
				helpDisplayed = true;
				textToFind.setText(helpText);
				typedText.setDisable(true);
				validateButton.setDisable(true);
			}
		});

		mediaView.setOnMouseClicked(ActionEvent -> {
			if (mediaView.getMediaPlayer().getStatus().equals(Status.PAUSED)) {
				mediaView.getMediaPlayer().play();
			} else {
				mediaView.getMediaPlayer().pause();
			}
		});

		soundSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if (mediaView.getMediaPlayer() != null) {
					mediaView.getMediaPlayer().setVolume(soundSlider.getValue() / 100);
				}

			}
		});
		validateButton.setOnAction(ActionEvent -> verify(typedText.getText()));
	}

	@FXML
	private void finishHandle() {
		((PopupController)popupLoader.getController()).reset();
		popupStage.show();
	}

	private void verify(String text) {
		if (text == null) {
			return;
		}
		String[] encrypted = encryptedText.split("[ \\t\\n\\x0B\\f\\r]");
		String[] clear = clearText.split("[ \\t\\n\\x0B\\f\\r]");

		Pattern punctionLessPattern = Pattern.compile("[^\\p{Punct}&&[^'-]]*");
		Matcher clearMatcher;
		for (int i = 0; i < clear.length; i++) {
			clearMatcher = punctionLessPattern.matcher(clear[i]);
			if (clearMatcher.find() && clearMatcher.group(0).equals(text)) {
				if (letterCase && !clearMatcher.group(0).toLowerCase().equals(text.toLowerCase())) {
					continue;
				}
				encrypted[i]=clear[i];
			}
			Pattern numberCharPattern = Pattern.compile(".{"+numberPartialReplacement+"}");
			Matcher numberCharMatcher = numberCharPattern.matcher(clear[i]);
			if (numberCharMatcher.find() && numberCharMatcher.group(0).equals(text) && numberPartialReplacement >0 && numberCharMatcher.group().matches(".{4,}")) {
				encrypted[i] = numberCharMatcher.group(0);
				for (int j = 2; j < clearMatcher.group(0).length(); j++) {
					encrypted[i] += occultationChar;
				}
				encrypted[i] += clear[i].substring(clearMatcher.group(0).length());
			}
		}
		//Reconstruction
		encryptedText = "";
		int length =0;
		for (int i = 0; i < encrypted.length; i++) {
			encryptedText += encrypted[i];
			if (length + clear[i].length() < clearText.length()) {
				length += clear[i].length();
			}
			if (Character.isWhitespace(clearText.charAt(length)) || Character.isSpaceChar(clearText.charAt(length))) {
				encryptedText += clearText.charAt(length);
			}
			length++;
		}
		if (isRealTime) {
			int numberWord = clear.length;
			int numberFoundWord = 0;
			for (String string : encrypted) {
				if (!string.contains("#")) {
					numberFoundWord++;
				}
			}
			realTime.setText(numberFoundWord +" mots trouvés/" + numberWord);
		}
		textToFind.setText(encryptedText);

	}

	private String clearText;

	char occultationChar;
	boolean mediaType = true;

	private int numberPartialReplacement = 0;
	private int minutes;
	private int seconds;
	private boolean exam =false;
	private String exerciseName;
	private boolean letterCase = false;
	private boolean isRealTime = false;

	public void parseExercise(File file) throws IOException {
		int nbBytesToRead;
		String helpString;
		String instructionString;
		String occultation;
		byte[] parameter;

		exerciseName = FileUtil.stripExtension(file);
		FileInputStream fin = new FileInputStream(file);

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		clearText = convertByteToString(fin.readNBytes(nbBytesToRead));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		helpString = convertByteToString(fin.readNBytes(nbBytesToRead));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		instructionString = convertByteToString(fin.readNBytes(nbBytesToRead));

		parameter = fin.readNBytes(1);
		occultation = convertByteToString(fin.readNBytes(1));
		occultationChar = occultation.charAt(0);

		minutes = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		seconds = ByteBuffer.wrap(fin.readNBytes(4)).getInt();

		FileOutputStream fos = null;
		FileOutputStream fos2 = null;
		if (getBit(parameter[0], 6) == 1) {
			fos = new FileOutputStream("temp.mp4");
			mediaType = true;
		} else {
			mediaType = false;
			fos = new FileOutputStream("temp.mp3");
			fos2 = new FileOutputStream("temp.png");
		}
		int bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
		fos.write(fin.readNBytes(bytesRead));

		if (getBit(parameter[0], 6) == 0) {
			bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
			fos2.write(fin.readNBytes(bytesRead));
		}
		fos.close();
		fin.close();


		if (getBit(parameter[0], 0) == 1) {
			exam = true;
		}else {
			minutes = 0;
			seconds = 0;
			if (getBit(parameter[0], 1) == 1) {
				if (getBit(parameter[0], 2) == 1) {
					numberPartialReplacement = 3;
				} else {
					numberPartialReplacement = 2;
				}
			}
			if (getBit(parameter[0], 4) == 1) {
				isRealTime = true;
				realTime.setVisible(true);
			}
		}
		if (getBit(parameter[0], 3) == 1) {
			letterCase  = true;
		}

		helpText = helpString;
		instructionText.setText(instructionString);
	}

	private int getBit(byte b, int pos) {
		return (b >> pos) & 1;
	}

	private String convertByteToString(byte[] readNBytes) {
		String buildString = "";
		for (int i = 0; i < readNBytes.length; i++) {
			buildString += (char) readNBytes[i];
		}
		return buildString;
	}


	private String encryptText() {
		String constructString = "";
		for (String string : clearText.split("")) {
			if (string.matches("[a-zA-Z]") || (string.matches("[0-9]"))) {
				constructString += occultationChar;
			}else {
				constructString += string;
			}
		}
		return constructString;
	}


	public void setSoluce() {
		textToFind.setText(clearText);
		finishButton.setVisible(false);
	}


	public void setExercise(String exName) throws IOException {
		File ex = new File(Main.getParameterController().getCreatedExercisePath().getAbsolutePath() + "/" + exName);
		parseExercise(ex);
		timeText.setText("Temps restant : " + minutes + ":" + seconds + "s");
		try {
			encryptedText = encryptText();
			textToFind.setText(encryptedText);
			File mediaSelected;
			if (mediaType) {
				mediaSelected = new File("temp.mp4");
				imageView.setVisible(false);
			} else {
				mediaSelected = new File("temp.mp3");
				File imageTemp = new File("temp.png");
				Image image = new Image(imageTemp.toURI().toString());
				imageView.setImage(image);
			}

			mediaView.setMediaPlayer(new MediaPlayer(new Media(mediaSelected.toURI().toURL().toExternalForm())));
			soundSlider.setValue(mediaView.getMediaPlayer().getVolume() * 100);
			InvalidationListener sliderChangeListener = o -> {
				Duration seekTo = Duration.seconds(progressBar.getValue());
				mediaView.getMediaPlayer().seek(seekTo);
			};
			progressBar.valueProperty().addListener(sliderChangeListener);
			mediaView.getMediaPlayer().currentTimeProperty().addListener(l -> {
				progressBar.valueProperty().removeListener(sliderChangeListener);
				Duration currentTime = mediaView.getMediaPlayer().getCurrentTime();
				progressBar.setValue(currentTime.toSeconds());
				progressBar.valueProperty().addListener(sliderChangeListener);
			});

			mediaView.getMediaPlayer().setOnReady(new Runnable() {

				@Override
				public void run() {
					progressBar.setMax(mediaView.getMediaPlayer().getTotalDuration().toSeconds());

				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveExercise() throws IOException {
		byte[] clearTextBytes = clearText.getBytes();
		byte[] clearTextLength = getLenght(clearText);

		byte[] encryptedTextBytes = encryptedText.getBytes();
		byte[] encryptedTextLenght = getLenght(encryptedText);

		byte[] instructionTextBytes = instructionText.getText().getBytes();
		byte[] instructionTextLength = getLenght(instructionText.getText());

		byte[] media = null;
		byte[] image = null;

		int imageLength = 0;

		FileInputStream mediaFileinput;
		FileInputStream imageFileinput = null;
		if (mediaType) {
			mediaFileinput = new FileInputStream("temp.mp4");
		}else {
			mediaFileinput = new FileInputStream("temp.mp3");
			if (imageView.getImage() != null) {
				imageFileinput = new FileInputStream("temp.png");
				image = imageFileinput.readAllBytes();
				imageLength = image.length;
			}
		}

		media = mediaFileinput.readAllBytes();
		int mediaLength = media.length;
		FileOutputStream fos = new FileOutputStream(Main.getParameterController().getStudentExercisePath().getAbsolutePath() + "/" + exerciseName + "_" + fileName + ".student");
		fos.write(ByteBuffer.allocate(4).putInt(mediaType? 1:0).array());

		fos.write(clearTextLength);
		fos.write(clearTextBytes);
		fos.write(encryptedTextLenght);
		fos.write(encryptedTextBytes);
		fos.write(instructionTextLength);
		fos.write(instructionTextBytes);

		fos.write(ByteBuffer.allocate(8).putInt(mediaLength).array());
		fos.write(media);

		fos.write(ByteBuffer.allocate(8).putInt(imageLength).array());
		if (imageFileinput != null) {
			fos.write(image);
			imageFileinput.close();
		}
		mediaFileinput.close();
		fos.close();
	}


	private byte[] getLenght(String input) {
		int count = 0;
		for(int i=0; i<input.length();i++){  
			count++;
		}
		return ByteBuffer.allocate(4).putInt(count).array();
	}

	public boolean isExam() {
		return exam;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private boolean startTimer;

	@FXML
	public void playPauseHandle() {
		if (mediaView.getMediaPlayer()== null) {
			return;
		}
		if (mediaView.getMediaPlayer().getStatus().equals(Status.PAUSED) || mediaView.getMediaPlayer().getStatus().equals(Status.READY)) {
			if (startTimer == false) {
				startTimer = true;
				timerCreation();
			}
			mediaView.getMediaPlayer().play();
			changePlayImage();
		}else {
			mediaView.getMediaPlayer().pause();
			changePlayImage();
		}
	}

	@FXML
	ImageView playPauseImage;

	@FXML
	ImageView muteImage;

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

	private Timeline timeline;

	private void timerCreation() {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		if (exam) {
			setDecreasingTimer(timeline);
		}else {
			setIncreasingTimeline(timeline);
		}
		timeline.playFromStart();
	}

	private void setDecreasingTimer(Timeline timeline) {
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1),
						new EventHandler<ActionEvent>() {
					// KeyFrame event handler
					@Override    
					public void handle(ActionEvent arg0) {
						seconds--;
						if (seconds < 0) {
							minutes--;
							seconds=59;
						}
						// update timerLabel
						timeText.setText("Temps restant : " + minutes + ":" + seconds + "s");
						if (seconds <= 0 && minutes <=0) {
							timeline.stop();
							popupStage.show();
							return;
						}

					}
				}));
	}

	private void setIncreasingTimeline(Timeline timeline) {
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(1),
						new EventHandler<ActionEvent>() {
					// KeyFrame event handler
					@Override    
					public void handle(ActionEvent arg0) {
						seconds++;
						if (seconds >59) {
							minutes++;
							seconds=0;
						}
						// update timerLabel
						timeText.setText("Temps restant : " + minutes + ":" + seconds + "s");
					}
				}));
	}
}
