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
import javafx.scene.text.Text;

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

	Text minute;
	
	@FXML
	TextField secondField;

	
	Text second;
	
	@FXML
	TextField occultationChoiceField;

	@FXML
	Spinner<Integer> numberOfLetterPartialReplacement;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		minuteField.setDisable(true);
		secondField.setDisable(true);
		minuteField.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				if (!newValue.matches("\\d*")) {
					minuteField.setText(newValue.replaceAll("[^\\d]", ""));
				}
				if (!newValue.matches("[0-9]{0,3}")) {
					newValue = newValue.replaceFirst("[0-9]{0,3}", "999");
					newValue = newValue.replaceAll(".$", "");
					minuteField.setText(newValue);
				}
			}
		});

		secondField.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, 
					String newValue) {
				if (!newValue.matches("\\d*")) {
					secondField.setText(newValue.replaceAll("[^\\d]", ""));
				}
				int testMax = 0;
				if (!newValue.equals("")) {
					testMax = Integer.parseInt(secondField.getText());
				}
				if (testMax>=60) {
					secondField.setText("59");
					minuteField.setText(String.valueOf(Integer.parseInt(minuteField.getText())+1));
				}
			}
		});

		trainingRadioButton.setSelected(true);

		trainingRadioButton.setOnAction(ActionEvent -> 
		{
			trainingRadioButton.setSelected(true);
			examRadioButton.setSelected(false);
			minuteField.setDisable(true);
			secondField.setDisable(true);
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
			minuteField.setDisable(false);
			secondField.setDisable(false);
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
		int minute = 0;
		if (minuteField.getText() != "" || minuteField.getText() != null) {
			minute = Integer.parseInt(minuteField.getText());
		}
		return ByteBuffer.allocate(4).putInt(minute).array();
	}

	public byte[] getSecond() {
		int seconds = 0;
		if (minuteField.getText() != "" || minuteField.getText() != null) {
			seconds = Integer.parseInt(minuteField.getText());
		}
		return ByteBuffer.allocate(4).putInt(seconds).array();
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

	public void setMinute(int minute) {
		minuteField.setText(String.valueOf(minute));
	}
	
	public void setSecond(int second) {
		secondField.setText(String.valueOf(second));
	}
	
	private int getBit(byte b, int pos) {
		return (b >> pos) & 1;
	}
}
