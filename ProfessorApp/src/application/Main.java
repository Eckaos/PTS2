package application;
	
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	static List<BorderPane> screens = new ArrayList<>();
	static int currentIndex = 0;
	static Scene scene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			screens.add((BorderPane)FXMLLoader.load(getClass().getResource("Accueil.fxml")));
			screens.add((BorderPane)FXMLLoader.load(getClass().getResource("ExerciceEditor.fxml")));
			scene = new Scene(screens.get(0),400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setMaximized(true);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static BorderPane getScreen(int index) {
		return screens.get(index);
	}
	
	public static void setScreen(int index) {
		scene.setRoot(screens.get(index));
		currentIndex = index;
	}
}
