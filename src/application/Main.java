package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
  //deixando uma referencia da sena para ser visualizado por outra tela
	
	private static Scene mainScene; 
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			//linha de codigo para ajustar o scrollPane ao layout
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Metodo para pegar a sena 
	public static Scene getMainScene() {
		
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
