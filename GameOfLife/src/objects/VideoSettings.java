package objects;

import java.io.File;

public class VideoSettings {

	private int fps;
	private int generation;
	private File file;

	/**
	 * @param fps
	 * @param generation
	 */
	public VideoSettings(int fps, int generation, File file) {
		this.fps = fps;
		this.generation = generation;
		this.file = file;
	}
	public int getFps() {
		return fps;
	}
	public int getGeneration() {
		return generation;
	}
	public File getFile() {
		return file;
	}
	
	@Override
	public String toString() {
		return "VideoSettings [fps=" + fps + ", generation=" + generation + "]";
	}
	
	
	
}
