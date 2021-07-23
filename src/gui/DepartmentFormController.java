package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController  implements Initializable{
    
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML 
	private Label lableErroName;
	
	@FXML
	private Button btSaving;
	@FXML
	private Button btCancel;
	
	
	@FXML
	public void onbtSavingAction() {
		
		System.out.println("onbtSavingAction");
	}
	
	@FXML
	public void onbtCancelAction() {
		System.out.println("onbtCancelAction");
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNode();
		
	}

	
	private void initializaNode() {
		
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		
		
	}
	
}
