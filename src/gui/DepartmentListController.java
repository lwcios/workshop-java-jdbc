package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
//Declarando uma dependência do serviço de departamento
	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tablecolumnName;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;

	@FXML
	public void onbtNewAction(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Department obj = new Department();
		crateDialogForm(obj,"/gui/DepartmentForm.fxml",parentStage);
		
		

	}

	/*
	 * Usando um Método para fazer a injeção de dependencia para evitar usar
	 * acoplamento forte através de instanciação de um new Department()
	 */
	public void setDepartmentService(DepartmentService service) {
		this.service = service;

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Metodo auxiliar para inicia a table view
		initializeNodes();

	}

	private void initializeNodes() {
		// Usa-se esse padrão no javafx para iniciar o comportamneto das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<Department, Integer>("id"));
		tablecolumnName.setCellValueFactory(new PropertyValueFactory<Department, String>("Name"));

		/*
		 * Ajustando a altura datable view 1º pegamos uma referencia para a sena através
		 * do metodo getMainScene() 2º depois chamamos o método getWindow() que é uma
		 * super classe do stage para pegarmos a janela 3º posteriormente fazemos um
		 * downcast para o stage
		 * 
		 */
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// fazendo a tableview receber as propriedades da altura da janela
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	/*
	 * Metodo responsável por acessar o service, carregar os departamentos e colocar
	 * os departamentos na observablelist
	 */
	public void updateTableView() {
		// teste para no caso de o programador se esquecer de instancia a list
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Department> list = service.findall();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	//função para criar uma caixa de diálogo
	private void crateDialogForm(Department obj,String absolutName,Stage parentStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader((getClass().getResource(absolutName)));
			Pane pane =loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();
			
			Stage dialogstage = new Stage();
			dialogstage.setTitle("Enter Department Data");
			dialogstage.setScene(new Scene(pane));
			dialogstage.setResizable(false);
			dialogstage.initOwner(parentStage);
			dialogstage.initModality(Modality.WINDOW_MODAL);
			dialogstage.showAndWait();
			
		}catch(IOException e) {
			Alerts.showAlert("IO Exception","Error Loading view",e.getMessage(), AlertType.ERROR);
		}
		
	}

}
