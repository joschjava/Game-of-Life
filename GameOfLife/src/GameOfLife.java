import java.awt.GridLayout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameOfLife /*extends Application */{

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model m = new Model(5,5);
		m.incSurroundingPoints(1, 2);
		m.createLivingCell(1, 1);
		m.createLivingCell(2, 1);
		m.createLivingCell(1, 2);
		m.createLivingCell(2, 2);

		for (int i = 0; i < 5; i++) {
			m.printCurGen();
			System.out.println();
			m.setNextGeneration();
		}
//		for (int i = 0; i < p.length; i++) {
//			System.out.println(p[i]);
//		}
	}

//	@Override
//	public void start(Stage primaryStage) throws Exception {		
//		
////        FXMLLoader loader = new FXMLLoader();
////        URL res = GameOfLife.class.getResource("/mainwindow.fxml");
////        loader.setLocation(res);
////        VBox rootLayout = null;
////		try {
////			rootLayout = (VBox) loader.load();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}	
//		GridLayout grid = new GridLayout();
//			
//		Scene scene = new Scene(grid);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//		
//	}
	
}
