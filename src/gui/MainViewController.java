package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable {
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {

		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		/*
		 * passando como parâmetro uma expressão lambda do tipo departmentListcontroller
		 * para inicializar o departmentListcontroller
		 */
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub

	}

	/*
	 * função para carregar uma nova tela o synchronized é para evitar a interrupção
	 * do do serviço durante o multu thread * Aqui passamos como parâmetro o
	 * consumer<t> para que a função load view funcione com as alterações feitas,
	 * onde passamos uma função lambda como argumento
	 */
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));

			VBox newVBox = loader.load();
			// carregando a sena pricipal
			Scene mainScene = Main.getMainScene();
			/*
			 * para que possamos passar os filhos do vbox about para os filhos do vbox da
			 * janela principal, teremos declarar um vbox que recebe o vbox da main O método
			 * getRoot() pega o primeiro elemento da main view que pela hierarquia é o
			 * scrollpane para isso fazemos o casting
			 */

			/*
			 * O getContent() já é uma referencia para tudo que estiver dentro dele
			 * inclusive o vbox dai basta fazer o casting
			 */

			VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			/*
			 * Aqui guardamos uma referencia dos filhos do vbox da tela inicial o
			 * get(posição) indica a ordem
			 */
			Node mainMenu = mainVbox.getChildren().get(0);

			// limpando os filhos do mainvbox
			mainVbox.getChildren().clear();

			// adicionando o vbox principal e depois o vbox do newvbox
			mainVbox.getChildren().add(mainMenu);
			mainVbox.getChildren().addAll(newVBox.getChildren());

			/*Nessas duas linhas fazemos com que seja executada a função que passamos 
			 * como parâmetro no loadview*/
			T controller = loader.getController();
			initializingAction.accept(controller);

		} catch (IOException e) {

			Alerts.showAlert("IOException", "Error loading View", e.getMessage(), Alert.AlertType.ERROR);
		}
	}

}
