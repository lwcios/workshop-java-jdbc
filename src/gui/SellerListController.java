package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
//Declarando uma dependência do serviço de departamento
	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tablecolumnName;
    
	@FXML
	private TableColumn<Seller, String> tablecolumnEmail;
	@FXML
	private TableColumn<Seller, Date> tablecolumnBirthDate;
	@FXML
	private TableColumn<Seller, Double> tablecolumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList;

	@FXML
	public void onbtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);

	}

	/*
	 * Usando um Método para fazer a injeção de dependencia para evitar usar
	 * acoplamento forte através de instanciação de um new Seller()
	 */
	public void setSellerService(SellerService service) {
		this.service = service;

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Metodo auxiliar para inicia a table view
		initializeNodes();

	}

	private void initializeNodes() {
		// Usa-se esse padrão no javafx para iniciar o comportamneto das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<Seller, Integer>("id"));
		tablecolumnName.setCellValueFactory(new PropertyValueFactory<Seller, String>("Name"));
		tablecolumnEmail.setCellValueFactory(new PropertyValueFactory<Seller, String>("email"));
		tablecolumnBirthDate.setCellValueFactory(new PropertyValueFactory<Seller, Date>("birthDate"));
		Utils.formatTableColumnDate(tablecolumnBirthDate, "dd/MM/yyyy");
		tablecolumnBaseSalary.setCellValueFactory(new PropertyValueFactory<Seller, Double>("baseSalary"));
       Utils.formatTableColumnDouble(tablecolumnBaseSalary, 2 );
		/*
		 * Ajustando a altura datable view 1º pegamos uma referencia para a sena através
		 * do metodo getMainScene() 2º depois chamamos o método getWindow() que é uma
		 * super classe do stage para pegarmos a janela 3º posteriormente fazemos um
		 * downcast para o stage
		 * 
		 */
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// fazendo a tableview receber as propriedades da altura da janela
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
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

		List<Seller> list = service.findall();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	// função para criar uma caixa de diálogo
	private void createDialogForm(Seller obj, String absolutName, Stage parentStage) {
		try {

			FXMLLoader loader = new FXMLLoader((getClass().getResource(absolutName)));
			Pane pane = loader.load();

			SellerFormController controller = loader.getController();
			controller.setSeller(obj);
			controller.setServices(new SellerService(),new DepartmentService());
		    controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogstage = new Stage();
			dialogstage.setTitle("Enter Seller Data");
			dialogstage.setScene(new Scene(pane));
			dialogstage.setResizable(false);
			dialogstage.initOwner(parentStage);
			dialogstage.initModality(Modality.WINDOW_MODAL);
			dialogstage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error Loading view", e.getMessage(), AlertType.ERROR);
		}

	}

	@Override
	public void onDataChanged() {

		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void  removeEntity(Seller obj) {
		
	Optional<ButtonType> result = Alerts.showConfirmation("confirmation","Are you sure to delete?");
	
	if(result.get() == ButtonType.OK) {
		if(service == null) {
			throw new IllegalStateException("service was null");
		}
		try {
		service.remove(obj);
		updateTableView();
		
		}catch(DbIntegrityException e) {
			
			Alerts.showAlert("Error removing obj", null,e.getMessage(), AlertType.ERROR);
		}
	}
		
	}

}
