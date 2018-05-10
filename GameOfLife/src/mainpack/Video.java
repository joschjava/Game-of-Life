package mainpack;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.MediaLocator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import gui.MainWindowController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import objects.VideoSettings;

public class Video {

	MainWindowController controller;
	VideoSettings options;
	
	public Video(MainWindowController controller, VideoSettings options){
		this.controller = controller;
		this.options = options;
	}
	
	public void createVideo() {
		System.out.println("Starting");
		
		Task worker = new Task (){
	          @Override
	            protected Object call() throws Exception {
	        	  Platform.runLater(new Runnable() {

					@Override
					public void run() {
						saveCurrentGameGrid();
						int generations = options.getGeneration();
						for (int i = 0; i < generations; i++) {
							controller.getModel().setNextGeneration();
							saveCurrentGameGrid();
							updateProgress(i + 1, generations);
							updateMessage(i + 1 + "/" + generations);
						}
						try {
							renderVideo(options.getFile(), options.getFps());
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
						System.out.println("Done");
					}

				}

				);

                return true;
	            }
		};
		controller.bindProgressBarToLoading(worker);
		new Thread(worker).start();
	}

	private void renderVideo(File dst, int fps) throws MalformedURLException {
	    Vector<String> imgLst = new Vector<String>();
	    File dir = new File(Const.VIDEO_TEMP_DIR);

		List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
	    
		files.forEach(file ->  {
			imgLst.add(file.getAbsolutePath());
		});
		
		if(imgLst.size() > 0) {
			File firstIm = files.get(0);
            BufferedImage bi;
			try {
				bi = ImageIO.read(firstIm);
		        //get width and height of image
	            int imageWidth = bi.getWidth();
	            int imageHeight = bi.getHeight();
	    	    VideoGenerator imageToMovie = new VideoGenerator();
	    	    MediaLocator oml;
	    	    if ((oml = VideoGenerator.createMediaLocator(dst.getName())) == null) {
	    	        System.err.println("Cannot build media locator from: " + dst.getName());
	    	    }
	    	    imageToMovie.doIt(imageWidth, imageHeight, fps, imgLst, oml);
	    	    File src = new File(dst.getName());
	            FileUtils.moveFile(src, dst);
	    	    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Error creating video: No images found in directory!");
		}
	}
	
	public void saveCurrentGameGrid() {
		WritableImage imageRaw = controller.getCurrentGridSnapshot();

		// Get buffered image:
		BufferedImage image = SwingFXUtils.fromFXImage(imageRaw, null);

		// Remove alpha-channel from buffered image:
		BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE);

		Graphics2D graphics = imageRGB.createGraphics();

		graphics.drawImage(image, 0, 0, null);

		graphics.dispose();
		File dir = new File(Const.VIDEO_TEMP_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String filename = String.format("%05d.jpg", controller.getModel().getGeneration());
		File file = new File(dir, filename);
		try {
			ImageIO.write(imageRGB, "jpg", file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
}
