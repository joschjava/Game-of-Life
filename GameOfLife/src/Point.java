
public class Point {
	private int x = 0;
	private int y = 0;
	
	Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return x+":"+y;
	}
}
