package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import javafx.util.Duration;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

	@FXML
	Button pausePlayButton;
	@FXML
	Slider progressBar;
	@FXML
	Button soundButton;
	@FXML
	Slider soundSlider;

	@FXML
	Button save;

	File file;
	
	
	FXMLLoader loader = new FXMLLoader(getClass().getResource("Parameter.fxml"));
	private byte[] getLenght(String input) {
		int count = 0;
		for(int i=0; i<input.length();i++){  
			count++;
		}
		return ByteBuffer.allocate(4).putInt(count).array();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		importButton.setOnAction(ActionEvent -> 
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
			filePath.setText(file.getAbsolutePath());
			try {
				importMediaView.setMediaPlayer(new MediaPlayer(new Media(file.toURI().toURL().toExternalForm())));
				soundSlider.setValue(importMediaView.getMediaPlayer().getVolume() * 100);
				InvalidationListener sliderChangeListener = o-> {
				    Duration seekTo = Duration.seconds(progressBar.getValue());
				    importMediaView.getMediaPlayer().seek(seekTo);
				};
				progressBar.valueProperty().addListener(sliderChangeListener);
				importMediaView.getMediaPlayer().currentTimeProperty().addListener(l-> {
				    progressBar.valueProperty().removeListener(sliderChangeListener);
				    Duration currentTime = importMediaView.getMediaPlayer().getCurrentTime();
				    progressBar.setValue(currentTime.toSeconds());
				    progressBar.valueProperty().addListener(sliderChangeListener);
				});
				
				importMediaView.getMediaPlayer().setOnReady(new Runnable() {
					
					@Override
					public void run() {
						progressBar.setMax(importMediaView.getMediaPlayer().getTotalDuration().toSeconds());
						
					}
				});
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		});
		

		Stage stage = new Stage();
		BorderPane root = new BorderPane();
		
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		parameter.setOnAction(ActionEvent -> 
		{
			stage.show();
		});


		pausePlayButton.setOnAction(ActionEvent -> 
		{
			if (importMediaView.getMediaPlayer()== null) {
				return;
			}
			if (importMediaView.getMediaPlayer().getStatus().equals(Status.PAUSED) || importMediaView.getMediaPlayer().getStatus().equals(Status.READY)) {
				importMediaView.getMediaPlayer().play();
			}else {
				importMediaView.getMediaPlayer().pause();
			}
		});

		soundButton.setOnMousePressed(ActionEvent -> 
		{
			if (importMediaView.getMediaPlayer().isMute()) {
				importMediaView.getMediaPlayer().setMute(false);
			}else {
				importMediaView.getMediaPlayer().setMute(true);
			}

		});

		soundSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if (importMediaView.getMediaPlayer() != null) {
					importMediaView.getMediaPlayer().setVolume(soundSlider.getValue() / 100);
				}

			}
		});

		save.setOnAction(ActionEvent -> {
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void save() throws IOException {
		ParameterController test = loader.getController();
		byte[] textBinary = text.getText().getBytes();
		byte[] lenghtText = getLenght(text.getText());
		byte[] instructionBinary = instruction.getText().getBytes();
		byte[] lenghtInstruction = getLenght(instruction.getText());
		byte[] helpBinary = help.getText().getBytes();
		byte[] lenghtHelp = getLenght(help.getText());

		byte parameters = 0;
		byte[] occultationChar;
		byte[] exerciceTime;
		parameters = test.getParameters();
		occultationChar = test.getOccultationCharacter();
		exerciceTime = test.getTime();



		FileOutputStream fos = new FileOutputStream(title.getText()+".bin");
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		fos.write(lenghtText);
		fos.write(textBinary);

		fos.write(lenghtHelp);
		fos.write(helpBinary);

		fos.write(lenghtInstruction);
		fos.write(instructionBinary);

		//TODO ecriture des parametres
		//fos.write(parameters);
		fos.write(occultationChar);
		//fos.write(exerciceTime);

		if (file != null) {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] media = new byte[4*1024];
			int bytesRead;
			while (fileInputStream.available() != 0) {
				bytesRead = fileInputStream.read(media);
				fos.write(media, 0, bytesRead);
			}
			fileInputStream.close();
		}
		fos.close();
	}
}
