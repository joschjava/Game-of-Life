package objects;

public class VideoSettings {

	int fps;
	int generation;
	
	
	
	/**
	 * @param fps
	 * @param generation
	 */
	public VideoSettings(int fps, int generation) {
		this.fps = fps;
		this.generation = generation;
	}
	public int getFps() {
		return fps;
	}
	public int getGeneration() {
		return generation;
	}
	@Override
	public String toString() {
		return "VideoSettings [fps=" + fps + ", generation=" + generation + "]";
	}
	
	
	
}
