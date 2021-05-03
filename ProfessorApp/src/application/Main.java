package application;
	
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	static BorderPane root;
	static List<AnchorPane> screens = new ArrayList<>();
	static int currentIndex;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			root = (BorderPane)FXMLLoader.load(getClass().getResource("GUIProfessor.fxml"));
			Scene scene = new Scene(root,400,400);
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
	
	public static AnchorPane getScreen(int index) {
		return screens.get(index);
	}
	
	public static void setScreen(int index) {
		root.getChildren().remove(screens.get(currentIndex));
		root.getChildren().add(screens.get(index));
		currentIndex = index;
	}
}
