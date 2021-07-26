package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController  implements Initializable{
    
	private Department entity;
	
	private DepartmentService service;
	
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
	
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		
		this.service = service;
	}
	
	
	@FXML
	public void onbtSavingAction() {
		
		entity = getFormData();
		service.saveOrUpdate(entity);
	
		
	}
	
	private Department getFormData() {
		Department obj = new Department();
		obj.setId(gui.util.Utils.tryparseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onbtCancelAction() {
		System.out.println("onbtCancelAction");
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializaNode();
		
	}

	@FXML
	private void initializaNode() {
		
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		
		
	}
	
	
	
	public void updateFormData() {
		
		if(entity ==null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		
	}
	
}
