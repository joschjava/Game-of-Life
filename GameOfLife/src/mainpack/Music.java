package mainpack;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

	private static MediaPlayer mediaPlayer;

	public void playMusic() {
		String musicFile = "C:\\Musik\\Amazing_OneEskimo.mp3";
		Media sound = new Media(new File(musicFile).toURI().toString());
		mediaPlayer = new MediaPlayer(sound);
		mediaPlayer.play();
	}
	
	public void stopMusic() {
		
	}
	
}
