package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class ParameterController implements Initializable{

	@FXML
	RadioButton trainingRadioButton;
	
	@FXML
	RadioButton examRadioButton;
	
	@FXML
	CheckBox partialReplacementCheckBox;
	
	@FXML
	CheckBox letterCaseCheckBox;
	
	@FXML
	CheckBox realTimeNumberWordCheckBox;
	
	@FXML
	CheckBox solutionPresenceCheckBox;
	
	@FXML
	Spinner<Integer> timeSpinner;
	
	@FXML
	TextField occultationChoiceField;
	
	@FXML
	Spinner<Integer> numberOfLetterPartialReplacement;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		trainingRadioButton.setSelected(true);
		
		trainingRadioButton.setOnAction(ActionEvent -> 
		{
			examRadioButton.setSelected(false);
		});
		
		examRadioButton.setOnAction(ActionEvent -> 
		{
			trainingRadioButton.setSelected(false);
			partialReplacementCheckBox.setDisable(true);
			letterCaseCheckBox.setDisable(true);
			realTimeNumberWordCheckBox.setDisable(true);
			solutionPresenceCheckBox.setDisable(true);
		});
		
		occultationChoiceField.setText("#");
		SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
		
		timeSpinner.setValueFactory(valueFactory);
		
		SpinnerValueFactory<Integer> valueFactory2 = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 3, 2);
		numberOfLetterPartialReplacement.setValueFactory(valueFactory2);
		numberOfLetterPartialReplacement.setVisible(false);
		
		partialReplacementCheckBox.setOnAction(ActionEvent -> 
		{
			if (partialReplacementCheckBox.isSelected()) {
				numberOfLetterPartialReplacement.setVisible(true);
			}else {
				numberOfLetterPartialReplacement.setVisible(false);
			}
		});
		
	}
	
	
	
	
}
