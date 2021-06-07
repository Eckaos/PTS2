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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
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

	@FXML private MediaView mediaView;
	@FXML private Button importMediaButton;
	@FXML private TextArea instruction;
	@FXML private TextArea help;
	@FXML private TextArea text;
	@FXML private Text mediaPath;
	@FXML private Text imagePath;
	@FXML private Button pausePlayButton;
	@FXML private ImageView playPauseImage;
	@FXML private Slider progressBar;
	@FXML private Button muteButton;
	@FXML private ImageView muteImage;
	@FXML private Slider soundSlider;
	@FXML private Button save;
	@FXML private Button importImageButton;
	@FXML private ImageView imageView;
	@FXML private MenuItem newExercise;
	@FXML private MenuItem modifExercise;
	@FXML private MenuItem seeResults;
	@FXML private MenuItem close;
	@FXML private MenuItem parameterMenuItem;
	@FXML private Label errorLabel;

	private File mediaFile;
	private File image;
	private FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseParameter.fxml"));
	private Stage parameterStage = new Stage();
	private BorderPane parameterRoot;

	private File fileToModify;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorLabel.setVisible(false);
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

		try {
			parameterRoot = (BorderPane) loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Scene parameterScene = new Scene(parameterRoot);
		parameterStage.setResizable(false);
		parameterStage.setScene(parameterScene);
		parameterStage.initModality(Modality.APPLICATION_MODAL);
		parameterScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	}

	public void save(String name) throws IOException{
		if ("".equals(name) || name == null) {
			return;
		}
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

		if (".mp4".equals(FileUtil.getExtension(mediaFile))) {
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
			fos = new FileOutputStream(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+name+".train");
		}else {
			fos = new FileOutputStream(Main.getParameterController().getCreatedExercisePath().getAbsolutePath()+"/"+name+".exam");
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
		
		int bytesRead = 0;
		FileInputStream fileInputStream1 = new FileInputStream(mediaFile);
		if (mediaFile != null) {
			byte[] mediaFile = fileInputStream1.readAllBytes();
			bytesRead = mediaFile.length;
		}
		fos.write(ByteBuffer.allocate(8).putInt(bytesRead).array());
		if (bytesRead > 0) {
			fos.write(fileInputStream1.readAllBytes());
		}
		fileInputStream1.close();
		fos.write(ByteBuffer.allocate(8).putInt(imageLenght).array());
		if (image != null) {
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

		parameterController.setTitle(FileUtil.stripExtension(file));
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
			File tempFile = new File("image/pauseButton.png");
			Image imageTemp = new Image(tempFile.toURI().toString()); 
			playPauseImage.setImage(imageTemp);
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

	@FXML
	public void saveHandle() throws IOException {
		if (instruction.getText().equals("") || text.getText().equals("")) {
			errorLabel.setVisible(true);
			return;
		}
		if (!instruction.getText().equals("") && !text.getText().equals("")) {
			errorLabel.setVisible(false);
		}
		parameterStage.show();
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

	public void reset() {
		help.setText("");
		text.setText("");
		instruction.setText("");
	}

}
