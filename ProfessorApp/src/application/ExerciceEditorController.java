package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		importButton.setOnAction(ActionEvent -> 
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
			filePath.setText(file.getAbsolutePath());
			try {
				importMediaView.setMediaPlayer(new MediaPlayer(new Media(file.toURI().toURL().toExternalForm())));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		});
		Stage stage = new Stage();
		BorderPane root = null;
		try {
			root = (BorderPane)FXMLLoader.load(getClass().getResource("Parameter.fxml"));
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
	}

}
