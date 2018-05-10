package mainpack;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GameOfLife extends Application {

	
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {		
		
        FXMLLoader loader = new FXMLLoader();
        URL res = GameOfLife.class.getResource("/mainwindow.fxml");
        loader.setLocation(res);
        HBox rootLayout = null;
		try {
			rootLayout = (HBox) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		GridPane grid = new GridPane();
		rootLayout.getChildren().add(grid);
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
}
