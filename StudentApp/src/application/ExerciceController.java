package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;

public class ExerciceController implements Initializable {

	@FXML
	private MediaView mediaView;
	@FXML
	private Text instructionText;
	@FXML
	private Button pausePlayButton;
	@FXML
	private Slider progressBar;
	@FXML
	private Button soundButton;

	@FXML
	private Text timeText;
	@FXML
	private Button helpButton;
	@FXML
	private Text textToFind; //TODO Change name
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
	
	//TODO demander le nom d'un etudiant au chargement d'un exercice exam
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
		
		pausePlayButton.setOnAction(ActionEvent -> {
			if (mediaView.getMediaPlayer() == null) {
				return;
			}
			if (mediaView.getMediaPlayer().getStatus().equals(Status.PAUSED)) {
				mediaView.getMediaPlayer().play();
			} else {
				mediaView.getMediaPlayer().pause();
			}
		});

		soundButton.setOnMousePressed(ActionEvent -> {
			if (mediaView.getMediaPlayer().isMute()) {
				mediaView.getMediaPlayer().setMute(false);
			} else {
				mediaView.getMediaPlayer().setMute(true);
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


		finishButton.setOnAction(ActionEvent -> Main.getPopupStage().show());

	}

	private void verify(String text) {
		if (text == null) {
			return;
		}
		String[] encrypted = encryptedText.split("[ \\t\\n\\x0B\\f\\r]");
		String[] clear = clearText.split("[ \\t\\n\\x0B\\f\\r]");
		String[] space = clearText.split("[^\n]*");
		

		System.out.println(space.length);
		for (String string : space) {
			System.out.println(string + " here");
		}
		Pattern punctionLessPattern = Pattern.compile("[^\\p{Punct}&&[^'-]]*");
		Matcher clearMatcher;
		for (int i = 0; i < clear.length; i++) {
			clearMatcher = punctionLessPattern.matcher(clear[i]);
			if (clearMatcher.find() && clearMatcher.group(0).equals(text)) {
				encrypted[i]=clear[i];
			}
			Pattern numberCharPattern = Pattern.compile(".{"+numberPartialReplacement+"}");
			Matcher numberCharMatcher = numberCharPattern.matcher(clear[i]);
			if (numberCharMatcher.find() && numberCharMatcher.group(0).equals(text) && numberPartialReplacement >0) {
				encrypted[i] = numberCharMatcher.group(0);
				for (int j = 2; j < clearMatcher.group(0).length(); j++) {
					encrypted[i] += occultationChar;
				}
				encrypted[i] += clear[i].substring(clearMatcher.group(0).length());
			}
		}
		//Reconstruction
		encryptedText = "";
		int i = 1;
		for (String string : encrypted) {
			if (i < space.length) {
				encryptedText += string + "\n";
			}else {
				encryptedText += string + " ";
			}
			i++;
		}
		textToFind.setText(encryptedText);
	}

	private String clearText;

	char occultationChar;
	boolean mediaType = true;

	private int numberPartialReplacement = 0;
	private int minutes;
	private int seconds;
	
	public void parseExercise(File file) throws IOException {
		int nbBytesToRead;
		String helpString;
		String instructionString;
		String occultation;
		byte[] parameter;

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

		if (getBit(parameter[0], 1) == 1) {
			if (getBit(parameter[0], 2) == 1) {
				numberPartialReplacement = 3;
			} else {
				numberPartialReplacement = 2;
			}
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
			mediaView.getMediaPlayer().setAutoPlay(true);
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

	private void saveExercise() throws IOException {
		byte[] clearTextBytes = clearText.getBytes();
		byte[] clearTextLength = getLenght(clearText);
		
		byte[] encryptedTextBytes = encryptedText.getBytes();
		byte[] encryptedTextLenght = getLenght(encryptedText);
		
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
		
		FileOutputStream fos = new FileOutputStream(Main.getParameterController().getStudentExercisePath().getAbsolutePath() + "/" + fileName + ".student");
		fos.write(clearTextLength);
		fos.write(clearTextBytes);
		
		fos.write(encryptedTextLenght);
		fos.write(encryptedTextBytes);
		
		fos.write(ByteBuffer.allocate(8).putInt(mediaLength).array());
		fos.write(media);
		
		
		if (imageFileinput != null) {
			fos.write(ByteBuffer.allocate(8).putInt(imageLength).array());
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
}
