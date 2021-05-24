package application;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

	static Parent root;
	static List<BorderPane> screens = new ArrayList<>();
	static int currentIndex = 0;
	static Scene scene;
	static Scene popupScene;

	private static FXMLLoader parameterLoader;
	private static ParameterController parameterController;
	private static FXMLLoader accueilLoader;
	private static FXMLLoader exLoader;
	private static FXMLLoader popupLoader;

	static Stage popupStage;
	
	@Override
	public void start(Stage primaryStage){
		try {
			
			popupLoader = new FXMLLoader(getClass().getResource("FinishPopupEx.fxml"));
			BorderPane popupScreen = (BorderPane) popupLoader.load();
			popupStage = new Stage();
			popupScene = new Scene(popupScreen);
			popupStage.setResizable(false);
			popupStage.setScene(popupScene);
			popupStage.initModality(Modality.APPLICATION_MODAL);

			parameterLoader = new FXMLLoader(getClass().getResource("Parameter.fxml"));
			accueilLoader = new FXMLLoader(getClass().getResource("Accueil.fxml"));
			exLoader = new FXMLLoader(getClass().getResource("Exercice.fxml"));
			parameterLoader.load();
			parameterController = parameterLoader.getController();
			screens.add((BorderPane)accueilLoader.load());
			screens.add((BorderPane)exLoader.load());
			scene = new Scene(screens.get(0),1280,720);
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
		scene.setRoot(screens.get(index));
		currentIndex = index;
	}

	public static ParameterController getParameterController() {
		return parameterController;
	}

	public static AccueilController getAccueilController() {
		return accueilLoader.getController();
	}

	public static ExerciceController getExController() {
		return exLoader.getController();
	}
	
	public static PopupController getPopupController() {
		return popupLoader.getController();
	}

	public static Stage getPopupStage() {
		return popupStage;
	}


}
