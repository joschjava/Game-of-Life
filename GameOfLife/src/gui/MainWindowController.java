package gui;


import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import mainpack.CellChangedListener;
import mainpack.Model;

public class MainWindowController implements CellChangedListener{

	@FXML
	GridPane gameGrid;
	
	@FXML
	Button btStep;
	
	@FXML
	Button btPlay;
	
	@FXML
	Button btClear;
	
	@FXML
	Button btReset;
	
	@FXML 
	Label lbGen;
	
	@FXML 
	Label lbSpeed;
	
	@FXML
	Slider slSpeed;
	
	Model m;
	
	Button buttons[][];

	private Timeline ticker;
	
    @FXML
    public void initialize() {
    	generateGrid(20,20);
    	btStep.setOnMouseClicked((me) -> {
    		m.setNextGeneration();
    	});
		ticker = new Timeline(
				new KeyFrame(Duration.millis(0), ae -> {
					m.setNextGeneration();
				}),
				new KeyFrame(new Duration(1000))
				);
		ticker.setCycleCount(Timeline.INDEFINITE);
        ticker.rateProperty()
        .bind(slSpeed.valueProperty().divide(100./19).add(1));
		
        
    	btPlay.setOnMouseClicked((me) -> {
    		if(ticker.getStatus() == Status.STOPPED) {
    			ticker.play();
    		} else if(ticker.getStatus() == Status.RUNNING) {
    			ticker.stop();
    		}
		});
    	btPlay.textProperty().bind(
    			Bindings.when(
    					ticker.statusProperty()
    					.isEqualTo(Status.RUNNING))
    					.then("Stop")
    					.otherwise("Play")
    			);
    	btClear.setOnMouseClicked((me) -> {
    		m.clearGrid();
    	});
    
    	
    	btReset.disableProperty().bind(
    			Bindings.when(
	    			m.generationProperty().isEqualTo(0))
	    			.then(true)
	    			.otherwise(false)
    			);
    	
    	btReset.setOnMouseClicked((me) -> {
    		m.loadGeneration0();
    	});
    	
		NumberStringConverter converter = new NumberStringConverter() {
			@Override
			public String toString(Number value) {
				return String.valueOf(value);
			}
		};
		
		NumberStringConverter converterRound = new NumberStringConverter() {
			@Override
			public String toString(Number value) {
				return String.format("%.2f",value);
			}
		};
		
		lbGen.textProperty().bindBidirectional(m.generationProperty(), converter);
		lbSpeed.textProperty().bindBidirectional(ticker.rateProperty(), converterRound);
        


		
    }

    public void generateGrid(int xSize, int ySize) {
    	buttons = new Button[xSize][ySize];
    	m = new Model(xSize,ySize);

    	for (int y = 0; y < ySize; y++) {
        	for (int x = 0; x < xSize; x++) {
        		Button b = new Button(" ");
        		b.getProperties().put("gridX", x);
        		b.getProperties().put("gridY", y);
        		
        		b.setOnMousePressed((me) -> {
        			m.toggleLivingCell((int)b.getProperties().get("gridX"),
        					(int)b.getProperties().get("gridY"));
        			m.resetGenCount();
        		});
        		b.setOnDragDetected((me) -> {
        			b.startFullDrag();
        		});
        		b.setOnMouseDragEntered((me) -> {
        				m.createLivingCell((int)b.getProperties().get("gridX"),
            					(int)b.getProperties().get("gridY"));
        		});
        		buttons[x][y] = b;
        		gameGrid.add(b, x,y);
    		}
		}
    	m.createLivingCell(1, 1);
    	m.addListener(this);
    }
    
	@Override
	public void cellChanged(int x, int y, boolean alive) {
		if(alive) {
			buttons[x][y].setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		} else {
			buttons[x][y].setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		}
	}
}
