package application;
	
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	private static List<BorderPane> screens = new ArrayList<>();
	private static int currentIndex = 0;
	private static Scene scene;
	
	private static FXMLLoader modifyLoader;
	private static FXMLLoader exerciseEditorLoader;
	private static FXMLLoader parameterLoader;
	private static ParameterController parameterController;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			parameterLoader = new FXMLLoader(getClass().getResource("Parameter.fxml"));
			parameterLoader.load();
			parameterController = parameterLoader.getController();
			modifyLoader = new FXMLLoader(getClass().getResource("ModifyExercise.fxml"));
			exerciseEditorLoader = new FXMLLoader(getClass().getResource("ExerciceEditor.fxml"));
			screens.add((BorderPane)FXMLLoader.load(getClass().getResource("Accueil.fxml"))); //index 0
			screens.add((BorderPane)modifyLoader.load()); //index 1
			screens.add((BorderPane)exerciseEditorLoader.load()); //index 2*/
			scene = new Scene(screens.get(0),1280,720);
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
	
	public static BorderPane getCurrentScreen() {
		return screens.get(currentIndex);
	}
	
	public static FXMLLoader getModifyLoader() {
		return modifyLoader;
	}
	
	public static FXMLLoader getExerciseEditorLoader() {
		return exerciseEditorLoader;
	}
	
	public static ParameterController getParameterController() {
		return parameterController;
	}
}
