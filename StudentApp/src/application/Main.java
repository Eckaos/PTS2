package application;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

	private static List<BorderPane> screens = new ArrayList<>();
	private static Scene scene;

	private static FXMLLoader parameterLoader;
	private static FXMLLoader accueilLoader;
	private static FXMLLoader exLoader;
	
	private static List<Scene> scenes = new ArrayList<>();
	
	private static Stage parameterStage;
	private BorderPane parameterScreen;
	private Scene parameterScene;
	
	@Override
	public void start(Stage primaryStage){
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
			parameterStage.setAlwaysOnTop(true);
			parameterScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			accueilLoader = new FXMLLoader(getClass().getResource("Accueil.fxml"));
			exLoader = new FXMLLoader(getClass().getResource("Exercice.fxml"));
			
			screens.add((BorderPane)accueilLoader.load());
			screens.add((BorderPane)exLoader.load());
			scene = new Scene(screens.get(0),1280,720);
			scenes.add(scene);
			scenes.add(parameterScene);
			Main.getParameterController().setStyleSheet();
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
		scene.setRoot(screens.get(index));
	}

	public static ParameterController getParameterController() {
		return parameterLoader.getController();
	}

	public static AccueilController getAccueilController() {
		return accueilLoader.getController();
	}

	public static ExerciceController getExController() {
		return exLoader.getController();
	}

	public static List<Scene> getScenes() {
		return scenes;
	}

	public static Stage getParameterStage() {
		return parameterStage;
	}
	public static Scene getScene() {
		return scene;
	}
}
