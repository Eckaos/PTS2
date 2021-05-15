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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	@FXML
	Button importButton;
	
	@FXML
	Slider soundSlider;
	
	@FXML
	ImageView imageView;

	String encryptedText;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		importButton.setOnAction(ActionEvent -> 
		{
			try {
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
				parseExercise(file);
				wordsText = textString.split(" ");
				encryptedText = encryptText();
				textToFind.setText(encryptedText);
				File mediaSelected;
				if (mediaType) {
					mediaSelected = new File("C:/Users/NathanPollart/git/PTS2/StudentApp/temp.mp4");
					imageView.setVisible(false);
				}else {
					mediaSelected = new File("C:/Users/NathanPollart/git/PTS2/StudentApp/temp.mp3");
					Image image = new Image("file:C:/Users/NathanPollart/git/PTS2/StudentApp/temp.png");
					imageView.setImage(image);
				}
				
				testMe.setMediaPlayer(new MediaPlayer(new Media(mediaSelected.toURI().toURL().toExternalForm())));
				testMe.getMediaPlayer().setAutoPlay(true);
				soundSlider.setValue(testMe.getMediaPlayer().getVolume() * 100);
				InvalidationListener sliderChangeListener = o-> {
				    Duration seekTo = Duration.seconds(progressBar.getValue());
				    testMe.getMediaPlayer().seek(seekTo);
				};
				progressBar.valueProperty().addListener(sliderChangeListener);
				testMe.getMediaPlayer().currentTimeProperty().addListener(l-> {
				    progressBar.valueProperty().removeListener(sliderChangeListener);
				    Duration currentTime = testMe.getMediaPlayer().getCurrentTime();
				    progressBar.setValue(currentTime.toSeconds());
				    progressBar.valueProperty().addListener(sliderChangeListener);
				});
				
				testMe.getMediaPlayer().setOnReady(new Runnable() {
					
					@Override
					public void run() {
						progressBar.setMax(testMe.getMediaPlayer().getTotalDuration().toSeconds());
						
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}

		});



		pausePlayButton.setOnAction(ActionEvent -> 
		{
			if (testMe.getMediaPlayer()== null) {
				return;
			}
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

		});

		testMe.setOnMouseClicked(ActionEvent -> 
		{
			if (testMe.getMediaPlayer().getStatus().equals(Status.PAUSED)) {
				testMe.getMediaPlayer().play();
			}else {
				testMe.getMediaPlayer().pause();
			}
		});

		soundSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if (testMe.getMediaPlayer() != null) {
					testMe.getMediaPlayer().setVolume(soundSlider.getValue() / 100);
				}

			}
		});
		validateButton.setOnAction(ActionEvent -> verify(typedText.getText()));

		//TODO faire la popup
		finishButton.setOnAction(null);
	}

	private void verify(String text) {
		//TODO faire la verif pour le parametre 
		String string = "";
		for (int i = 0; i < wordsText.length; i++) {
			if (wordsText[i].equals(text)) {
				wordsTextEncrypted[i] = wordsText[i];
			}
			string += wordsTextEncrypted[i] +" ";
		}
		if (textString.equals(typedText.getText())) {
			typedText.setText(textString);
			string = textString;
		}
		textToFind.setText(string);
		typedText.setText("");
	}
	String textString;
	String[] wordsText;
	
	char occultationChar;
	boolean mediaType =true;;
	public void parseExercise(File file) throws IOException {
		int nbBytesToRead;
		String helpString;
		String instructionString;
		String occultation;
		byte[] parameter;
		int minutes;
		int seconds;
		
		FileInputStream fin = new FileInputStream(file);

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		textString = convertByteToString(fin.readNBytes(nbBytesToRead));
		
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
			fos = new FileOutputStream("C:/Users/NathanPollart/git/PTS2/StudentApp/temp.mp4");
			mediaType = true;
		}else {
			mediaType =false;
			fos = new FileOutputStream("C:/Users/NathanPollart/git/PTS2/StudentApp/temp.mp3");
			fos2 = new FileOutputStream("C:/Users/NathanPollart/git/PTS2/StudentApp/temp.png");
		}
		int bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
		fos.write(fin.readNBytes(bytesRead));
		
		if (getBit(parameter[0], 6) == 0) {
			bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
			fos2.write(fin.readNBytes(bytesRead));
		}
		fos.close();
		fin.close();
	
		instructionText.setText(instructionString);
		
	}

	private int getBit(byte b, int pos) {
		return (b >> pos) & 1;
	}

	private String convertByteToString(byte[] readNBytes) {
		String buildString ="";
		for (int i = 0; i < readNBytes.length; i++) {
			buildString += (char) readNBytes[i];
		}
		return buildString;
	}
	
	String[] wordsTextEncrypted;
	private String encryptText() {
		char[] test = textString.toCharArray();
		String string = "";
		for (int i = 0; i < test.length; i++) {
			if (test[i] != ' ' && test[i] != '\'') {
				string += occultationChar;
			}else if(test[i] == ' '){
				string += ' ';
			}else if (test[i] == '\'') {
				string += '\'';
			}
		}
		wordsTextEncrypted = string.split(" ");
		return string;
	}

}
