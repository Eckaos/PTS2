package application;
	
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	static Parent root;
	static List<BorderPane> screens = new ArrayList<>();
	static int currentIndex = 0;
	
	private static FXMLLoader parameterLoader;
	private static ParameterController parameterController;
	
	@Override
	public void start(Stage primaryStage){
		try {
			
			parameterLoader = new FXMLLoader(getClass().getResource("Parameter.fxml"));
			parameterLoader.load();
			parameterController = parameterLoader.getController();
			screens.add((BorderPane)FXMLLoader.load(getClass().getResource("Exercice.fxml")));
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
	
	public static ParameterController getParameterController() {
		return parameterController;
	}
}
