package gui;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import mainpack.ImageAnalyser;
import objects.VideoSettings;

public class ImageDialog {

	private Node btSubmit;
	private File file;
	private Slider slThreshold;
	private ImageAnalyser ia = ImageAnalyser.getInstance();
	
	public void showVideoDialog(MainWindowController controller){
		// Create the custom dialog.
		Dialog<VideoSettings> dialog = new Dialog<>();
		dialog.setTitle("Generate Grid From Image...");

		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		
		
		Label lbFileLocation = new Label("");
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		Stage stage = (Stage) controller.getMainPane().getScene().getWindow();
//		ExtensionFilter extFilter = new ExtensionFilter("Quicktime (*.mov)", "*.mov");
//		fileChooser.getExtensionFilters().add(extFilter);
		
		slThreshold = new Slider();
		slThreshold.setMin(0);
		slThreshold.setMax(255);
		slThreshold.setDisable(true);
		slThreshold.setValue(123);
		
		file = ia.getPreviousFile();
		if(file != null) {
			File prevDir = new File(file.getParentFile().getPath());
			fileChooser.setInitialDirectory(prevDir);
			lbFileLocation.setText(file.getName());
			slThreshold.setDisable(false);
			slThreshold.setValue(ia.getThreshold());
		}
		
		
		
		Button btFile = new Button("Select File Location...");
		btFile.setOnMouseClicked(me -> {
			File selected = fileChooser.showOpenDialog(stage);
			if(selected != null) {
				file = selected;
				String parent = file.getParent();
				String name = file.getName();
				
				fileChooser.setInitialDirectory(new File(parent));
				fileChooser.setInitialFileName(name);
				lbFileLocation.setText(name);
				
				ia = ImageAnalyser.getInstance();
				int threshold = 123;
				boolean[][] gridData = ia.convertImage(file, controller.getModel(), threshold);
				slThreshold.setValue(threshold);
				controller.getModel().setGrid(gridData);
				slThreshold.setDisable(false);
			}
		});
		

		Label lbSliderValue = new Label();
		lbSliderValue.setWrapText(false);
		NumberStringConverter converter = new NumberStringConverter() {
			@Override
			public String toString(Number value) {
				return String.valueOf(value.intValue());
			}
		};
		
		lbSliderValue.textProperty().bindBidirectional(slThreshold.valueProperty(), converter);
		grid.add(btFile, 0, 0);
		grid.add(lbFileLocation, 1, 0);
		grid.add(new Label("Threshold"), 0, 1);
		grid.add(slThreshold, 1, 1);
		grid.add(lbSliderValue, 2, 1);

		
		

		dialog.getDialogPane().setContent(grid);

		

       slThreshold.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
            	System.out.println(file.toString());
            	if(file != null) {
	            	int value = newValue.intValue();
	        		boolean[][] gridData = ia.adjustThreshold(value);
	        		controller.getModel().setGrid(gridData);
	        		controller.resetChart();
            	}
            }
        });
		dialog.showAndWait();
	}
	

	
}
