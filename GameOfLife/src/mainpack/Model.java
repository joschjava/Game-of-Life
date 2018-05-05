package mainpack;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Model {

	private boolean curGen[][];
	private boolean nextGen[][];
	private boolean genZero[][];
	
	private int numNeighbours[][];
	private int xSize = -1;
	private int ySize = -1;
	
	private IntegerProperty generation = new SimpleIntegerProperty(0);
	
	private ArrayList<CellChangedListener> listeners = new ArrayList<CellChangedListener>();
	
	public Model (int xSize, int ySize){
		this.xSize = xSize;
		this.ySize = ySize;
		curGen = new boolean[ySize][xSize];
		numNeighbours = new int[ySize][xSize];

		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				curGen[y][x] = false;
				numNeighbours[y][x] = 0;
			}
		}
	}
	
	public IntegerProperty generationProperty() {
		return generation;
	}
	
	private void saveGeneration0() {
		genZero = curGen.clone();
	}
	
	public void loadGeneration0() {
		curGen = genZero.clone();
		resetGenCount();
		notifyBoardChange();
	}
	
	public void resetGenCount() {
		generation.set(0);
	}
	
	public void addListener(CellChangedListener listener) {
		listeners.add(listener);
		notifyBoardChange();
	}
	
	public void notifyListener(int x, int y, boolean alive) {
		for(CellChangedListener l: listeners) {
			l.cellChanged(x, y, alive);
		}
	}
	
	public void setNextGeneration() {
		if(generation.get() == 0) {
			saveGeneration0();
		}
		nextGen = new boolean[ySize][xSize];
		resetNeighbours();
		calcNumNeighbours();
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				int num = numNeighbours[y][x];
				if(num < 2 && curGen[y][x]) {
					nextGen[y][x] = false;
				} else if((num == 2 || num == 3) && curGen[y][x]) {
					nextGen[y][x] = true;
				} else if(num > 3 && curGen[y][x]) {
					nextGen[y][x] = false; 
				} else if(num == 3 && !curGen[y][x]) {
					nextGen[y][x] = true;
				} else {
					nextGen[y][x] = false;
				}
			}
		}
		generation.set(generation.get()+1); 
		curGen = nextGen;
		notifyBoardChange();
	}

	private void calcNumNeighbours() {
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				if(curGen[y][x]) {
					incSurroundingPoints(x,y);
				}
			}
		}
	}
	
	/**
	 * Sets neighbour array to 0
	 */
	private void resetNeighbours() {
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				numNeighbours[y][x] = 0;
			}
		}
	}
	
	public void toggleLivingCell(int x, int y) {
		setCell(x,y,!curGen[y][x]);
	}
	
	public void createLivingCell(int x, int y) {
		setCell(x,y,true);
	}
	
	public void createDeadCell(int x, int y) {
		setCell(x,y,false);
	}
	
	public void clearGrid() {
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				setCell(x,y,false);
			}
		}
		resetGenCount();
	}
	
	private void setCell(int x, int y, boolean alive) {
		curGen[y][x] = alive;
		notifyListener(x, y, alive);
	}
	
	/**
	 * Increases the points surrounding particular point
	 * @param x
	 * @param y
	 * @return
	 */
	public void incSurroundingPoints(int x, int y){
		for(int xIt = -1; xIt <= 1; xIt++) {
			for(int yIt = -1; yIt <= 1; yIt++) {
				if(!(xIt == 0 && yIt ==0)) {
					int xCur = correctCoord(x+xIt, xSize);
					int yCur = correctCoord(y+yIt, ySize);
					try {
						numNeighbours[yCur][xCur]+=1;
					} catch (Exception e) {
						System.out.println();
					}
					
				}
			}
		}
	}

	/**
	 * Corrects coordinate if it is outside of grid
	 * @param coord Coordinate to be tested
	 * @param maxSize Maximum Size of Coordinate
	 * @return
	 */
	private int correctCoord(int coord, int maxSize) {
		if(coord < 0) {
			coord = maxSize-1;
		} else if(coord >= maxSize) {
			coord = 0;
		}
		return coord;
	}
	
	public void printCurGen(){
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				char out;
				if(curGen[y][x]) {
					out = '1';
				} else {
					out = '0';
				}
				System.out.print(out + " ");
			}
			System.out.println();
		}
	}
	
	public void notifyBoardChange() {
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				notifyListener(x, y, curGen[y][x]);
			}
		}
	}
	
	public boolean[][] getCurGenerationCopy() {
		return curGen.clone();
	}
	
	public void printNeighbours(){
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				System.out.print(numNeighbours[y][x] + " ");
			}
			System.out.println();
		}
	}
}
