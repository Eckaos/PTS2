package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import javafx.util.Duration;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExerciceEditorController implements Initializable{

	@FXML
	MediaView mediaView;
	@FXML
	Button importMediaButton;
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
	Text mediaPath;
	@FXML
	Text imagePath;

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

	@FXML
	MenuItem newExercise;

	@FXML
	MenuItem modifExercise;

	@FXML
	MenuItem seeResults;

	@FXML
	MenuItem close;

	@FXML
	MenuItem parameterMenuItem;

	File mediaFile;
	File image;
	FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseParameter.fxml"));
	Stage parameterStage = new Stage();
	BorderPane parameterRoot;

	private File fileToModify;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		importImageButton.setVisible(false);
		imagePath.setVisible(false);
		close.setOnAction(ActionEvent -> 
		{
			Stage stage = (Stage) mediaView.getScene().getWindow();
			stage.close();
		});

		newExercise.setOnAction(ActionEvent -> Main.setScreen(2));
		modifExercise.setOnAction(ActionEvent -> 
		{
			if (Main.getParameterController().getCreatedExercisePath() != null) {
				Main.setScreen(1);
			}
		});
		parameterMenuItem.setOnAction(ActionEvent -> Main.getParameterStage().show());
		
		parameter.setOnAction(ActionEvent -> parameterStage.show());

		try {
			parameterRoot = (BorderPane) loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Scene parameterScene = new Scene(parameterRoot);
		parameterStage.setResizable(false);
		parameterStage.setScene(parameterScene);
		parameterStage.initModality(Modality.APPLICATION_MODAL);
	}

	public void save() throws IOException{
		ExerciseParameterController test = loader.getController();
		byte[] textBinary = text.getText().getBytes();
		byte[] lenghtText = getLenght(text.getText());

		byte[] instructionBinary = instruction.getText().getBytes();
		byte[] lenghtInstruction = getLenght(instruction.getText());

		byte[] helpBinary = help.getText().getBytes();
		byte[] lenghtHelp = getLenght(help.getText());

		byte parameters = test.getParameters();
		byte[] occultationChar = test.getOccultationCharacter();
		byte[] exerciceMinute = test.getMinute();
		byte[] exerciceSeconds = test.getSecond();
		byte[] imageFile = null;
		int imageLenght = 0;

		if ("mp4".equals(FileUtil.getExtension(mediaFile))) {
			parameters |= (1<<6);
		}else {
			if (image != null) {
				FileInputStream imageInputStream = new FileInputStream(image);
				imageFile = imageInputStream.readAllBytes();
				imageLenght = imageFile.length;
				imageInputStream.close();
			}

		}
		FileOutputStream fos;
		if (fileToModify != null) {
			fos = new FileOutputStream(fileToModify);
		}else if(getBit(parameters, 0) == 0){
			fos = new FileOutputStream(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+title.getText()+".train");
		}else {
			fos = new FileOutputStream(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+title.getText()+".exam");
		}

		fos.write(lenghtText);
		fos.write(textBinary);

		fos.write(lenghtHelp);
		fos.write(helpBinary);

		fos.write(lenghtInstruction);
		fos.write(instructionBinary);

		fos.write(parameters);
		fos.write(occultationChar);
		fos.write(exerciceMinute);
		fos.write(exerciceSeconds);

		if (mediaFile != null) {
			FileInputStream fileInputStream1 = new FileInputStream(mediaFile);
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
	public void parseExercise(File file) throws IOException {
		ExerciseParameterController parameterController = loader.getController();
		int nbBytesToRead;
		byte[] parameter;
		int minutes;
		int seconds;

		title.setText(FileUtil.stripExtension(file));
		FileInputStream fin = new FileInputStream(file);

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		text.setText(convertByteToString(fin.readNBytes(nbBytesToRead)));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		help.setText(convertByteToString(fin.readNBytes(nbBytesToRead)));

		nbBytesToRead = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		instruction.setText(convertByteToString(fin.readNBytes(nbBytesToRead)));

		parameter = fin.readNBytes(1);
		parameterController.setParameters(parameter);


		parameterController.setOccultationChoiceField(convertByteToString(fin.readNBytes(1)));

		minutes = ByteBuffer.wrap(fin.readNBytes(4)).getInt();
		seconds = ByteBuffer.wrap(fin.readNBytes(4)).getInt();

		parameterController.setMinute(minutes);
		parameterController.setSecond(seconds);

		FileOutputStream fos = null;
		FileOutputStream fos2 = null;
		if (getBit(parameter[0], 6) == 1) {
			fos = new FileOutputStream("temp.mp4");
		}else {
			fos = new FileOutputStream("temp.mp3");
			fos2 = new FileOutputStream("temp.png");
		}
		int bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
		fos.write(fin.readNBytes(bytesRead));

		File mediaFile;
		Media media;
		if (getBit(parameter[0], 6) == 0) {
			bytesRead = ByteBuffer.wrap(fin.readNBytes(8)).getInt();
			fos2.write(fin.readNBytes(bytesRead));
			mediaFile = new File("temp.png");
			imageView.setImage(new Image(mediaFile.toURI().toString()));
			mediaFile = new File("temp.mp3");
			media = new Media(mediaFile.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			setMediaListener(media);
			importImageButton.setVisible(true);
			imagePath.setVisible(true);
		}else {
			mediaFile = new File("temp.mp4");
			media = new Media(mediaFile.toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			setMediaListener(media);
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

	public void setImage(File image) {
		this.image = image;
	}

	public void setFileToModify(File fileToModify) {
		this.fileToModify = fileToModify;
	}

	private String path;
	private MediaPlayer mediaPlayer;
	@FXML
	private void OpenFileMethod(ActionEvent event) throws MalformedURLException {
		FileChooser fileChooser = new FileChooser();
		mediaFile = fileChooser.showOpenDialog(importMediaButton.getScene().getWindow());
		if (".mp3".equals(FileUtil.getExtension(mediaFile))) {
			importImageButton.setVisible(true);
			imagePath.setVisible(true);
		}
		if(mediaFile != null){
			path = mediaFile.toURI().toString();
			mediaPath.setText(mediaFile.toString());
			Media media = new Media(path);
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			setMediaListener(media);

			mediaPlayer.play();
		}
	}

	@FXML
	public void importImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		image = fileChooser.showOpenDialog(importMediaButton.getScene().getWindow());
		imagePath.setText(image.toString());
		imageView.setImage(new Image(image.toURI().toString()));
	}

	@FXML
	public void parmeterButtonHandle() {
		parameterStage.show();
	}

	@FXML
	public void playPauseHandle() {
		if (mediaView.getMediaPlayer()== null) {
			return;
		}
		if (mediaView.getMediaPlayer().getStatus().equals(Status.PAUSED) || mediaView.getMediaPlayer().getStatus().equals(Status.READY)) {
			mediaView.getMediaPlayer().play();
		}else {
			mediaView.getMediaPlayer().pause();
		}
	}

	@FXML
	public void muteHandle() {
		if (mediaView.getMediaPlayer()== null) {
			return;
		}
		if(mediaView.getMediaPlayer().isMute()) {
			mediaView.getMediaPlayer().setMute(false);
		}else {
			mediaView.getMediaPlayer().setMute(true);
		}
	}

	@FXML
	public void saveHandle() throws IOException {
		save();
	}

	private void setMediaListener(Media media) {
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
	}
}
