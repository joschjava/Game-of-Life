package mainpack;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Model {

	private boolean curGen[][];
//	private boolean nextGen[][];
	private boolean genZero[][];
	
	private int numNeighbours[][];
	private static int xSize = -1;
	private static int ySize = -1;
	
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
			}
		}
	}
	
	public IntegerProperty generationProperty() {
		return generation;
	}
	
	private void saveGeneration0() {
		genZero = new boolean[curGen.length][];
		for(int i = 0; i < curGen.length; i++)
		{
		  boolean[] aMatrix = curGen[i];
		  int   aLength = aMatrix.length;
		  genZero[i] = new boolean[aLength];
		  System.arraycopy(aMatrix, 0, genZero[i], 0, aLength);
		}
	}
	
	public int getGeneration() {
		return generation.get();
	}
	
	public int getLivingCells() {
		int numLivingCells = 0;
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				if(curGen[y][x]) {
					numLivingCells++;
				}
			}
		}
		return numLivingCells;
	}
	
	public void loadGeneration0() {		
		curGen = new boolean[genZero.length][];
		for(int i = 0; i < genZero.length; i++)
		{
		  boolean[] aMatrix = genZero[i];
		  int   aLength = aMatrix.length;
		  curGen[i] = new boolean[aLength];
		  System.arraycopy(aMatrix, 0, curGen[i], 0, aLength);
		}
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
	
	public int setNextGeneration() {
		int numCells = 0;
		if(generation.get() == 0) {
			saveGeneration0();
		}
//		nextGen = new boolean[ySize][xSize];
		resetNeighbours();
		calcNumNeighbours();
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				int num = numNeighbours[y][x];
				if(num < 2 && curGen[y][x]) {
					curGen[y][x] = false;
				} else if((num == 2 || num == 3) && curGen[y][x]) {
					curGen[y][x] = true;
					numCells++;
				} else if(num > 3 && curGen[y][x]) {
					curGen[y][x] = false; 
				} else if(num == 3 && !curGen[y][x]) {
					curGen[y][x] = true;
					numCells++;
				} else {
					curGen[y][x] = false;
				}
			}
		}
		generation.set(generation.get()+1); 
//		curGen = nextGen;
		notifyBoardChange();
		return numCells;
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
		numNeighbours = new int[ySize][xSize];
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
	
	public boolean getCell(int x, int y) {
		return curGen[y][x];
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
					int xCur = correctCoord(x+xIt, 'x');
					int yCur = correctCoord(y+yIt, 'y');
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
//	public static int correctCoord(int coord, int maxSize) {
//		if(coord < 0) {
//			coord = maxSize-1;
//		} else if(coord >= maxSize) {
//			coord = 0;
//		}
//		return coord;
//	}
	
	/**
	 * Corrects coordinate if it is outside of grid
	 * @param coord Coordinate to be tested
	 * @param direction 'x' or 'y'
	 * @return
	 */
	public static int correctCoord(int coord, char direction) {
		int maxSize = -1;
		if(direction == 'x') {
			maxSize = xSize;
		} else if(direction == 'y') {
			maxSize = ySize;
		} else {
			System.err.println("Wrong direction: "+direction+", Use 'x' or 'y'");
		}
		
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
