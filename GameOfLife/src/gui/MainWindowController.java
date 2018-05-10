package gui;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import mainpack.CellChangedListener;
import mainpack.Const;
import mainpack.Model;
import mainpack.Video;
import objects.VideoSettings;

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
	Button btPlace;
	
	@FXML
	Button btVideo;
	
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

	private boolean placeObject[][];
	private boolean placeActive = false;
	private ArrayList<Point> highlightedPoints = new ArrayList<Point>();
	
    @FXML
    public void initialize() {
    	generateGrid(30,25);
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
    	
    	btVideo.setOnMouseClicked((me) -> {
    		createVideo();
    	});
    	
    	btPlace.setOnMouseClicked((me) -> {
    		activatePlacingObjects();
    		btPlace.getScene().addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
    		      if(key.getCode()==KeyCode.ESCAPE) {
    		          deactivatePlacingObjects();
    		          gameGrid.setCursor(Cursor.DEFAULT);
    		      }
    		});
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

    private void createVideo() {
    	Optional<VideoSettings> settings = showVideoDialog();
    	settings.ifPresent(options -> {
    		saveAsFile();
		    for(int i=0;i < options.getGeneration();i++) {
		    	m.setNextGeneration();
		    	saveAsFile();
		    }
		    try {
				Video.makeVideo("test.mov", options.getFps());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				FileUtils.deleteDirectory(new File(Const.VIDEO_TEMP_DIR));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
    	
    }
    
    //TODO: Save video until current position
	private Optional<VideoSettings> showVideoDialog() {
		// Create the custom dialog.
		Dialog<VideoSettings> dialog = new Dialog<>();
		dialog.setTitle("Login Dialog");
		dialog.setHeaderText("Look, a Custom Login Dialog");


		// Set the button types.
		ButtonType btSubmit = new ButtonType("Submit", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(btSubmit, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField tfEndGen = new TextField("10");
		TextField tfFps = new TextField("1");

		grid.add(new Label("Last Generation:"), 0, 0);
		grid.add(tfEndGen, 1, 0);
		grid.add(new Label("Frames per Second:"), 0, 1);
		grid.add(tfFps, 1, 1);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if(dialogButton.equals(btSubmit)) {
				int endGen = Integer.parseInt(tfEndGen.getText());
				int fps = Integer.parseInt(tfFps.getText());
				
				return new VideoSettings(fps, endGen);
			} else {
				return null;
			}
		});
		
		return dialog.showAndWait();
	}

	private void saveAsFile() {
		WritableImage imageRaw = gameGrid.snapshot(new SnapshotParameters(), null);

		// Get buffered image:
		BufferedImage image = 
		  SwingFXUtils.fromFXImage(
				  imageRaw, 
		    null); 

		// Remove alpha-channel from buffered image:
		BufferedImage imageRGB = 
		  new BufferedImage(
		    image.getWidth(), 
		    image.getHeight(), 
		    BufferedImage.OPAQUE); 

		Graphics2D graphics = imageRGB.createGraphics();

		graphics.drawImage(
		  image, 
		  0, 
		  0, 
		  null);

		graphics.dispose();
		File dir = new File(Const.VIDEO_TEMP_DIR);
		if(!dir.exists()) {
			dir.mkdir();
		}
        File file = new File(dir, m.getGeneration()+".jpg");
        try {
            ImageIO.write(imageRGB, "jpg", file);
        } catch (IOException e) {
            // TODO: handle exception here
        }
	}

	private void setNextGeneration() {
        
		int numCells = m.setNextGeneration();
        series.getData().add(new XYChart.Data(m.generationProperty().get(), numCells));
        if(numCells == 0) {
        	ticker.stop();
        }
	}

    public void generateGrid(int xSize, int ySize) {
    	gameGrid.getChildren().clear();
    	gameGrid.setOnMouseExited((me) ->
    	{
    		if(placeActive) {
    			unhighlightObject();
    		}
    		gameGrid.setCursor(Cursor.DEFAULT);
    	});
    	gameGrid.setOnMouseEntered((me) ->
    	{
    		if(placeActive) {
    			gameGrid.setCursor(Cursor.NONE);
    		}
    	});
    	
    	buttons = new Button[xSize][ySize];
    	m = new Model(xSize,ySize);

    	for (int y = 0; y < ySize; y++) {
        	for (int x = 0; x < xSize; x++) {
        		Button b = new Button(" ");
        		b.getProperties().put("gridX", x);
        		b.getProperties().put("gridY", y);
        		
        		b.setOnMousePressed((me) -> {
        			if(placeActive) {
        		    	for(int yy=0;yy<placeObject.length;yy++) {
        		        	for(int xx=0;xx<placeObject[0].length;xx++) {
        		        		int xCur = xx+(int)b.getProperties().get("gridX");
        		        		int yCur = yy+(int)b.getProperties().get("gridY");
        		        		int correctedX = Model.correctCoord(xCur, 'x');
        		        		int correctedY = Model.correctCoord(yCur, 'y');
        		        		if(placeObject[yy][xx]) {
        		        			m.createLivingCell(correctedX, correctedY);
        		        		} else {
        		        			m.createDeadCell(correctedX, correctedY);
        		        		}
        		        	}
        		    	}
        			} else {
	        			m.toggleLivingCell((int)b.getProperties().get("gridX"),
	        					(int)b.getProperties().get("gridY"));
        			}
        			m.resetGenCount();
        			resetChart();
        		});
        		b.setOnDragDetected((me) -> {
        			if(!placeActive) {
						b.startFullDrag();
					}
        		});
        		b.setOnMouseDragEntered((me) -> {
        			if(!placeActive) {
	        			m.createLivingCell((int)b.getProperties().get("gridX"),
	        					(int)b.getProperties().get("gridY"));
	        			m.resetGenCount();
	        			resetChart();
        			}
        		});
        		
        		b.setOnMouseEntered((me) -> {
        			if(placeActive) {
        				previewObject((int)b.getProperties().get("gridX"),
            					(int)b.getProperties().get("gridY"));
        			}
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
    
    public void activatePlacingObjects() {
    	placeActive = true;
    	placeObject = new boolean[][] {{false, true, false},
							    		{false, false, true},
							    		{true, true, true}};
    }
    
    public void deactivatePlacingObjects() {
    	placeActive = false;
    	unhighlightObject();
    }
    
    public void previewObject(int xInit, int yInit) {
    	unhighlightObject();
    	for(int y=0;y<placeObject.length;y++) {
        	for(int x=0;x<placeObject[0].length;x++) {
        		int xCur = Model.correctCoord(x+xInit, 'x');
        		int yCur = Model.correctCoord(y+yInit, 'y');
				highlightPreviewCell(xCur, yCur, placeObject[y][x]);
				highlightedPoints.add(new Point(xCur, yCur));
        	}
    	}
    }

	private void unhighlightObject() {
		highlightedPoints.forEach((p) -> {
    		unhighlightPreviewCell((int)p.getX(), (int)p.getY());
    	});
    	highlightedPoints.clear();
	}
    
    public void highlightPreviewCell(int x, int y, boolean alive) {
		if(alive) {
			buttons[x][y].setBackground(Const.BG_LIVING_PRE);
		} else {
			buttons[x][y].setBackground(Const.BG_DEAD_PRE);
		}
    }
    
    public void unhighlightPreviewCell(int x, int y) {
    	boolean alive = m.getCell(x, y);
    	cellChanged(x,y,alive);
    }
    
	@Override
	public void cellChanged(int x, int y, boolean alive) {
		if(alive) {
			buttons[x][y].setBackground(Const.BG_LIVING);
		} else {
			buttons[x][y].setBackground(Const.BG_DEAD);
		}
	}
}
