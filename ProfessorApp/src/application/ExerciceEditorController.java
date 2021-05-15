package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import javafx.util.Duration;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
	Button muteButton;
	@FXML
	Slider soundSlider;
	@FXML
	Button save;
	@FXML
	Button importImageButton;
	@FXML
	ImageView imageView;

	File file;
	File image;
	FXMLLoader loader = new FXMLLoader(getClass().getResource("Parameter.fxml"));
	Stage parameterStage = new Stage();
	BorderPane parameterRoot;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			parameterRoot = (BorderPane) loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Scene scene = new Scene(parameterRoot);
		parameterStage.setResizable(false);
		parameterStage.setScene(scene);
		parameterStage.initModality(Modality.APPLICATION_MODAL);
	}

	public void save() throws IOException{
		ParameterController test = loader.getController();
		byte[] textBinary = text.getText().getBytes();
		byte[] lenghtText = getLenght(text.getText());

		byte[] instructionBinary = instruction.getText().getBytes();
		byte[] lenghtInstruction = getLenght(instruction.getText());

		byte[] helpBinary = help.getText().getBytes();
		byte[] lenghtHelp = getLenght(help.getText());

		byte parameters = 0;
		byte[] occultationChar;
		byte[] exerciceMinute;
		byte[] exerciceSeconds;

		parameters = test.getParameters();
		byte[] imageFile = null;
		int imageLenght = 0;
		if ("mp4".equals(getExtensionByStringHandling(file.getName()).get())) {
			parameters |= (1<<6);
		}else {
			if (image != null) {
				FileInputStream imageInputStream = new FileInputStream(image);
				imageFile = imageInputStream.readAllBytes();
				imageLenght = imageFile.length;
				imageInputStream.close();
			}

		}
		occultationChar = test.getOccultationCharacter();
		exerciceMinute = test.getMinute();
		exerciceSeconds = test.getSecond(); //TODO attention au parser pour le temps

		FileOutputStream fos = new FileOutputStream(title.getText()+".bin");

		fos.write(lenghtText);
		fos.write(textBinary);

		fos.write(lenghtHelp);
		fos.write(helpBinary);

		fos.write(lenghtInstruction);
		fos.write(instructionBinary);
		fos.write(parameters);
		fos.write(occultationChar);
		//fos.write(exerciceTime);

		if (file != null) {
			FileInputStream fileInputStream1 = new FileInputStream(file);
			int bytesRead = 0;
			byte[] mediaFile = fileInputStream1.readAllBytes();
			bytesRead = mediaFile.length;
			fos.write(ByteBuffer.allocate(8).putInt(bytesRead).array());
			fos.write(mediaFile);
			fileInputStream1.close();
		}
		if (image != null) {
			fos.write(ByteBuffer.allocate(8).putInt(imageLenght).array());
			fos.write(imageFile);
		}
		fos.close();
	}

	public Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	private boolean mediaType;
	public void parseExercise(File file) throws IOException {
		ParameterController test = loader.getController();
		int nbBytesToRead;
		byte[] parameter;
		int time;
		
		title.setText(FileUtil.stripExtension(file));
		FileInputStream fin = new FileInputStream(file);

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		text.setText(convertByteToString(fin.readNBytes(nbBytesToRead)));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		help.setText(convertByteToString(fin.readNBytes(nbBytesToRead)));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		instruction.setText(convertByteToString(fin.readNBytes(nbBytesToRead)));

		parameter = fin.readNBytes(1);

		test.setParameters(parameter);


		test.setOccultationChoiceField(convertByteToString(fin.readNBytes(1)));

		time = ByteBuffer.wrap(fin.readNBytes(4)).getInt();

		FileOutputStream fos = null;
		FileOutputStream fos2 = null;
		if (getBit(parameter[0], 6) == 1) {
			fos = new FileOutputStream("C:/Users/NathanPollart/git/PTS2/ProfessorApp/temp.mp4");
			mediaType = true;
		}else {
			mediaType =false;
			fos = new FileOutputStream("C:/Users/NathanPollart/git/PTS2/ProfessorApp/temp.mp3");
			fos2 = new FileOutputStream("C:/Users/NathanPollart/git/PTS2/ProfessorApp/temp.png");
		}
		int bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
		fos.write(fin.readNBytes(bytesRead));

		if (getBit(parameter[0], 6) == 0) {
			bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
			fos2.write(fin.readNBytes(bytesRead));
		}
		fos.close();
		fin.close();
	}

	private byte[] getLenght(String input) {
		int count = 0;
		for(int i=0; i<input.length();i++){  
			count++;
		}
		return ByteBuffer.allocate(4).putInt(count).array();
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

	public void setFile(File file) throws MalformedURLException {
		this.file = file;
		importMediaView.setMediaPlayer(new MediaPlayer(new Media(file.toURI().toURL().toExternalForm())));
		importMediaView.getMediaPlayer().setAutoPlay(true);
	}

	public void setImage(File image) {
		this.image = image;
	}

	private String path;
	private MediaPlayer mediaPlayer;
	@FXML
	private void OpenFileMethod(ActionEvent event) throws MalformedURLException {
		FileChooser fileChooser = new FileChooser();
		file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
		if(file != null){
			path = file.toURI().toString();
			filePath.setText(file.toURI().toURL().getPath());
			Media media = new Media(path);
			mediaPlayer = new MediaPlayer(media);
			importMediaView.setMediaPlayer(mediaPlayer);

			soundSlider.setValue(mediaPlayer.getVolume()*100);
			soundSlider.valueProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					mediaPlayer.setVolume(soundSlider.getValue()/100);
				}
			});

			mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
					progressBar.setValue(newValue.toSeconds());
				}
			});

			progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
				}
			});

			progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
				}
			});

			mediaPlayer.setOnReady(new Runnable() {
				@Override
				public void run() {
					Duration total = media.getDuration();
					progressBar.setMax(total.toSeconds());
				}
			});

			mediaPlayer.play();
		}
	}

	@FXML
	public void importImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		image = fileChooser.showOpenDialog(importButton.getScene().getWindow());
	}
	
	@FXML
	public void parmeterButtonHandle() {
		parameterStage.show();
	}
	
	@FXML
	public void playPauseHandle() {
		if (importMediaView.getMediaPlayer()== null) {
			return;
		}
		if (importMediaView.getMediaPlayer().getStatus().equals(Status.PAUSED) || importMediaView.getMediaPlayer().getStatus().equals(Status.READY)) {
			importMediaView.getMediaPlayer().play();
		}else {
			importMediaView.getMediaPlayer().pause();
		}
	}
	
	@FXML
	public void muteHandle() {
		if (importMediaView.getMediaPlayer()== null) {
			return;
		}
		if(importMediaView.getMediaPlayer().isMute()) {
			importMediaView.getMediaPlayer().setMute(false);
		}else {
			importMediaView.getMediaPlayer().setMute(true);
		}
	}
	
	@FXML
	public void saveHandle() throws IOException {
		save();
	}
}
