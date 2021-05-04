package application;
	
import java.util.ArrayList;
import java.util.List;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	static Parent root;
	static List<BorderPane> screens = new ArrayList<>();
	static int currentIndex;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load(getClass().getResource("GUIStudent.fxml"));
			screens.add((BorderPane)FXMLLoader.load(getClass().getResource("Accueil.fxml")));
			//root.getChildren().add(screens.get(0));
			Scene scene = new Scene(screens.get(0),1280,720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setMaximized(true);
			primaryStage.setMinHeight(720);
			primaryStage.setMinWidth(1280);
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
		currentIndex = index;
	}
}
