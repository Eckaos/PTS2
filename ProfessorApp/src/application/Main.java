package application;
	
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	private static List<BorderPane> screens = new ArrayList<>();
	private static int currentIndex = 0;
	private static Scene scene;
	
	private static FXMLLoader modifyLoader;
	private static FXMLLoader exerciseEditorLoader;
	private static FXMLLoader seeResultLoader;
	private static FXMLLoader resultScreenLoader;
	
	private static FXMLLoader parameterLoader;
	private BorderPane parameterScreen;
	private static Stage parameterStage;
	private Scene parameterScene;
	
	private static List<Scene> scenes = new ArrayList<>();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Auditrad");
			primaryStage.getIcons().add(new Image(getClass().getResource("/image/logo.png").toString()));
			parameterLoader = new FXMLLoader(getClass().getResource("Parameter.fxml"));
			parameterScreen = (BorderPane) parameterLoader.load();
			parameterStage = new Stage();
			parameterScene = new Scene(parameterScreen);
			parameterStage.setResizable(false);
			parameterStage.setScene(parameterScene);
			parameterStage.initModality(Modality.APPLICATION_MODAL);
			parameterScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			modifyLoader = new FXMLLoader(getClass().getResource("ModifyExercise.fxml"));
			exerciseEditorLoader = new FXMLLoader(getClass().getResource("ExerciceEditor.fxml"));
			seeResultLoader = new FXMLLoader(getClass().getResource("SeeResult.fxml"));
			resultScreenLoader = new FXMLLoader(getClass().getResource("ResultScreen.fxml"));
			
			screens.add((BorderPane)FXMLLoader.load(getClass().getResource("Accueil.fxml"))); //index 0
			screens.add((BorderPane)modifyLoader.load()); //index 1
			screens.add((BorderPane)exerciseEditorLoader.load()); //index 2*/
			screens.add((BorderPane)seeResultLoader.load());
			screens.add((BorderPane)resultScreenLoader.load());
			scene = new Scene(screens.get(0),1280,720);
			scenes.add(scene);
			((ParameterController) parameterLoader.getController()).setStyleSheet();
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
	
	public static ModifyExerciseController getModifyExerciseController() {
		return modifyLoader.getController();
	}
	
	public static ExerciceEditorController getExerciceEditorController() {
		return exerciseEditorLoader.getController();
	}
	
	public static ParameterController getParameterController() {
		return parameterLoader.getController();
	}
	public static SeeResultController getSeeResultController() {
		return seeResultLoader.getController();
	}
	
	public static ResultScreenController getResultScreenController() {
		return resultScreenLoader.getController();
	}
	
	public static Stage getParameterStage() {
		return parameterStage;
	}
	
	public static List<Scene> getScenes() {
		return scenes;
	}
	
	public static Scene getScene() {
		return scene;
	}
}
