package mainpack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageAnalyser {

	
	
	private int[][] gridAverage;

	public boolean[][] convertImage(File file, Model m, int threshold){
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
			int width = img.getWidth();
			int height = img.getHeight();
			int tilesPxX = (int) Math.ceil((double)width/m.getXsize());
			int tilesPxY = (int) Math.ceil((double)height/m.getYsize());
			int tilesX = m.getXsize();
			int tilesY = m.getYsize();
			
			boolean[][] grid = new boolean[tilesY][tilesX];
			int[][] gridValue = new int[tilesY][tilesX];
			int[][] gridNum = new int[tilesY][tilesX];
			gridAverage = new int[tilesY][tilesX];
			
			for(int y=0;y<height;y++) {
				for(int x=0;x<width;x++) {
					int value = getAverage(img.getRGB(x, y));
					int xGrid = (int)x/tilesPxX;
					int yGrid = (int)y/tilesPxY;
					gridValue[yGrid][xGrid] += value;					
					gridNum[yGrid][xGrid] ++;					
				}
			}
			
			for(int y = 0;y < tilesY;y++) {
				for(int x = 0;x < tilesX;x++) {
					//TODO: Something wrong: last line not accessed
					if(gridNum[y][x] != 0) {
						gridAverage[y][x] = gridValue[y][x] / gridNum[y][x];
						if(gridAverage[y][x] > threshold) {
							grid[y][x] = true;
						} else {
							grid[y][x] = false;
						}
					} else {
						grid[y][x] = true;
					}
				}
			}
			return grid;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	};
	
	public boolean[][] adjustThreshold(int threshold){

		if(gridAverage != null) {
			int tilesX = gridAverage[0].length;
			int tilesY = gridAverage.length;
			boolean[][] grid = new boolean[tilesY][tilesX];
			for(int y = 0;y < tilesY;y++) {
				for(int x = 0;x < tilesX;x++) {
					if(gridAverage[y][x] > threshold) {
						grid[y][x] = true;
					} else {
						grid[y][x] = false;
					}
				} 
			}
			return grid;
		} else {
			System.err.println("No Image selected!");
			return null;
		}
	}
	
	public int getAverage(int rgbValue) {
	    int red = (rgbValue >> 16) & 0xff;
	    int green = (rgbValue >> 8) & 0xff;
	    int blue = (rgbValue) & 0xff;
	    return (red+green+blue)/3;
	}
}
