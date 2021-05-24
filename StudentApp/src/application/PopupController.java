package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;



public class PopupController implements Initializable {
	
	@FXML
	Button non;
	@FXML
	Button oui;
	
	

	public void initialize(URL arg0, ResourceBundle arg1) {
		
		non.setOnAction(ActionEvent ->{
			((Stage) non.getScene().getWindow()).close();
		});
		
		oui.setOnAction(ActionEvent ->{

			ExerciceController soluceController = Main.getExController();
			soluceController.setSoluce();
			((Stage) oui.getScene().getWindow()).close();
		});
		
	}
}