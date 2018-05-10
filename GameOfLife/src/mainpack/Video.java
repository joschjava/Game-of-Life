package mainpack;

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

public class Video {

	public static void makeVideo(String fileName, int fps) throws MalformedURLException {
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
	    	    if ((oml = VideoGenerator.createMediaLocator(fileName)) == null) {
	    	        System.err.println("Cannot build media locator from: " + fileName);
	    	    }
	    	    imageToMovie.doIt(imageWidth, imageHeight, fps, imgLst, oml);
	            
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Error creating video: No images found in directory!");
		}



	}
	
}
