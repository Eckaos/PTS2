package application;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	TextField minuteField;
	
	@FXML
	TextField secondField;

	@FXML
	TextField occultationChoiceField;

	@FXML
	Spinner<Integer> numberOfLetterPartialReplacement;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		minuteField.textProperty().addListener(new ChangeListener<String>() {
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	minuteField.setText(newValue.replaceAll("[^\\d]", ""));
		        }
		    }
		});
		
		secondField.textProperty().addListener(new ChangeListener<String>() {
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*")) {
		        	if () {
		        		
					}else {
						
					}
		        	
		        }
		    }
		});
		
		trainingRadioButton.setSelected(true);

		trainingRadioButton.setOnAction(ActionEvent -> 
		{
			trainingRadioButton.setSelected(true);
			examRadioButton.setSelected(false);
			partialReplacementCheckBox.setDisable(false);
			letterCaseCheckBox.setDisable(false);
			realTimeNumberWordCheckBox.setDisable(false);
			solutionPresenceCheckBox.setDisable(false);
			numberOfLetterPartialReplacement.setDisable(false);
		});

		examRadioButton.setOnAction(ActionEvent -> 
		{
			examRadioButton.setSelected(true);
			trainingRadioButton.setSelected(false);
			partialReplacementCheckBox.setDisable(true);
			letterCaseCheckBox.setDisable(true);
			realTimeNumberWordCheckBox.setDisable(true);
			solutionPresenceCheckBox.setDisable(true);
			numberOfLetterPartialReplacement.setDisable(true);
		});

		occultationChoiceField.setText("#");
		minuteField.setText("1");
		secondField.setText("30");
		
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

	public byte getParameters() {
		byte parameters = 0;
		int position = 0;
		if (examRadioButton.isSelected()) {
			parameters |= (1<<position);
		}
		position++;
		if (partialReplacementCheckBox.isSelected()) {
			parameters |= (1<<position);
		}
		position++;
		if (numberOfLetterPartialReplacement.getValue() == 3) {
			parameters |= (1<<position);
		}
		position++;
		if (letterCaseCheckBox.isSelected()) {
			parameters |= (1<<position);
		}
		position++;
		if (realTimeNumberWordCheckBox.isSelected()) {
			parameters |= (1<<position);
		}
		position++;
		if (solutionPresenceCheckBox.isSelected()) {
			parameters |= (1<<position);
		}
		return parameters;
	}

	public byte[] getOccultationCharacter() {
		return occultationChoiceField.getText().getBytes();
	}

	public byte[] getMinute() {
		int minute = Integer.getInteger(minuteField.getText());
		return ByteBuffer.allocate(4).putInt(1).array();
	}
	
	public byte[] getSecond() {
		return ByteBuffer.allocate(4).putInt(1).array();
	}
	
	public void setModeRadioButton(boolean option) {
		if (option) {
			examRadioButton.setSelected(true);
		} else {
			trainingRadioButton.setSelected(true);
		}
		
	}
	
	public void setOccultationChoiceField(String occultationChar) {
		occultationChoiceField.setText(occultationChar);
	}
	
	public void setParameters(byte[] parameters) {
		int position = 0;
		if (getBit(parameters[0], position) == 1) {
			examRadioButton.setSelected(true);
			trainingRadioButton.setSelected(false);
			partialReplacementCheckBox.setDisable(true);
			letterCaseCheckBox.setDisable(true);
			realTimeNumberWordCheckBox.setDisable(true);
			solutionPresenceCheckBox.setDisable(true);
			numberOfLetterPartialReplacement.setDisable(true);
		} else {
			trainingRadioButton.setSelected(true);
			examRadioButton.setSelected(false);
		}
		position++;
		if (getBit(parameters[0], position) == 1) {
			partialReplacementCheckBox.setSelected(true);
		}
		position++;
		if (getBit(parameters[0], position) == 1) {
			//mettre le bon fonctionnement
		}
		position++;
		if (getBit(parameters[0], position) == 1) {
			letterCaseCheckBox.setSelected(true);
		}
		position++;
		if (getBit(parameters[0], position) == 1) {
			realTimeNumberWordCheckBox.setSelected(true);
		}
		position++;
		if (getBit(parameters[0], position) == 1) {
			solutionPresenceCheckBox.setSelected(true);
		}
	}
	
	private int getBit(byte b, int pos) {
		return (b >> pos) & 1;
	}
}
