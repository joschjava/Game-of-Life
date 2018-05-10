package gui;

import java.io.File;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import objects.VideoSettings;

public class VideoDialog {

	private File file;
	private TextField tfEndGen;
	private TextField tfFps;
	private final String numberRegex = "[0-9]++";
	private Node btSubmit;


	
 	public Optional<VideoSettings> showVideoDialog(MainWindowController controller) {
		// Create the custom dialog.
		Dialog<VideoSettings> dialog = new Dialog<>();
		dialog.setTitle("Generate Video...");

		ButtonType btSubmitType = new ButtonType("Submit", ButtonData.OK_DONE);		
		dialog.getDialogPane().getButtonTypes().addAll(btSubmitType, ButtonType.CANCEL);
		btSubmit = dialog.getDialogPane().lookupButton(btSubmitType);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));



		UnaryOperator<Change> filter = change -> {
		    String text = change.getText();
		
		    if (text.matches("[0-9]*")) {
		        return change;
		    }
		
		    return null;
		};
		tfEndGen = new TextField("10");
		tfFps = new TextField("1");
		tfEndGen.setTextFormatter(new TextFormatter<>(filter));
		tfFps.setTextFormatter(new TextFormatter<>(filter));
		
		tfEndGen.textProperty().addListener((observable, oldValue, newValue) -> {
			validateSubmit();
		});
		
		tfFps.textProperty().addListener((observable, oldValue, newValue) -> {
			validateSubmit();
		});
		
		
		Label lbFileLocation = new Label("");
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		Stage stage = (Stage) controller.getMainPane().getScene().getWindow();
		ExtensionFilter extFilter = new ExtensionFilter("Quicktime (*.mov)", "*.mov");
		fileChooser.getExtensionFilters().add(extFilter);
		
		
		Button btFile = new Button("Select File Location...");
		btFile.setOnMouseClicked(me -> {
			File selected = fileChooser.showSaveDialog(stage);
			if(selected != null) {
				file = selected;
				String parent = file.getParent();
				String name = file.getName();
				
				fileChooser.setInitialDirectory(new File(parent));
				fileChooser.setInitialFileName(name);
				lbFileLocation.setText(name);
				validateSubmit();
			}
		});
		
		grid.add(new Label("Last Generation:"), 0, 0);
		grid.add(tfEndGen, 1, 0);
		grid.add(new Label("Frames per Second:"), 0, 1);
		grid.add(tfFps, 1, 1);
		grid.add(btFile, 0, 2);
		grid.add(lbFileLocation, 1, 2);
		
		

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if(dialogButton.equals(btSubmitType)) {
				int endGen = Integer.parseInt(tfEndGen.getText());
				int fps = Integer.parseInt(tfFps.getText());
				return new VideoSettings(fps, endGen, file);
			} else {
				return null;
			}
		});
		validateSubmit();
		return dialog.showAndWait();
	}
	
 	/**
 	 * Checks if input is valid and submit button can be clicked
 	 */
 	private void validateSubmit() {
 		boolean valid = true;
 		if(!tfEndGen.getText().matches(numberRegex) ||
 		   !tfFps.getText().matches(numberRegex)	||
 		   file == null) {
 			valid = false;
 		}
 		btSubmit.setDisable(!valid);
 	}
 	
}
