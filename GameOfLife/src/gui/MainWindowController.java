package gui;


import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
	VBox mainPane;
	
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

	 XYChart.Series series = new XYChart.Series();
	 
	 int counter = 0;
	
	private Timeline ticker;
	
    @FXML
    public void initialize() {
    	generateGrid(40,25);
    	btStep.setOnMouseClicked((me) -> {
    		setNextGeneration();
    	});
		ticker = new Timeline(
				new KeyFrame(Duration.millis(0), ae -> {
					setNextGeneration();
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
    		resetChart();
    	});
    
    	
    	btReset.disableProperty().bind(
    			Bindings.when(
	    			m.generationProperty().isEqualTo(0))
	    			.then(true)
	    			.otherwise(false)
    			);
    	
    	btReset.setOnMouseClicked((me) -> {
    		m.loadGeneration0();
    		resetChart();
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
        
		createChart();
		m.addListener(this);
		
    }

	private void setNextGeneration() {
//		if(m.getGeneration() == 0) {
//			series.getData().add(new XYChart.Data(0, m.getLivingCells()));
//		}
		int numCells = m.setNextGeneration();
        series.getData().add(new XYChart.Data(m.generationProperty().get(), numCells));
        if(numCells == 0) {
        	ticker.stop();
        }
	}

    public void generateGrid(int xSize, int ySize) {
    	gameGrid.getChildren().clear();
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
        			resetChart();
        		});
        		b.setOnDragDetected((me) -> {
        			b.startFullDrag();
        		});
        		b.setOnMouseDragEntered((me) -> {
        				m.createLivingCell((int)b.getProperties().get("gridX"),
            					(int)b.getProperties().get("gridY"));
            			m.resetGenCount();
            			resetChart();
        		});
        		buttons[x][y] = b;
        		gameGrid.add(b, x,y);
    		}
		}
    	
    }
    
    private void createChart() {

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generation");
        yAxis.setLabel("Number living cells");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setAnimated(false);
        //defining a series
        series = new XYChart.Series();
        lineChart.setLegendVisible(false);
        //populating the series with data
        lineChart.getData().add(series);
        mainPane.getChildren().add(lineChart);
    }
    
    private void resetChart() {
    	int size = series.getData().size();
    	if(size>1 || size == 0) {
    		series.getData().clear();
        	XYChart.Data update = new XYChart.Data(0, m.getLivingCells());
        	series.getData().add(0, update);
    	} else {
    		((XYChart.Data) series.getData().get(0)).setYValue(m.getLivingCells());
    	}

    }
    
    
	@Override
	public void cellChanged(int x, int y, boolean alive, boolean firstGen) {
		if(alive) {
			buttons[x][y].setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		} else {
			buttons[x][y].setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		}
//		if(firstGen) {
//			System.out.println("Firstgen");
//			int length = series.getData().size();
//			if(length > 0) {
//				series.getData().remove(0);
//				XYChart.Data update = new XYChart.Data(0, m.getLivingCells());
//				series.getData().add(0, update);
//			}
//		}
	}
}
