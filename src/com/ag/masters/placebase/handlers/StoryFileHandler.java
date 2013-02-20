/**
 * Handles writing files to the SD card
 */

package com.ag.masters.placebase.handlers;	

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class StoryFileHandler {
	
	// intent request codes
	private static final int IMAGE_CAPTURE = 0;
	private static final int VIDEO_CAPTURE = 1;
	private static final int AUDIO_CAPTURE = 2;
	
	public static final String MEDIA_DIRECTORY = "placebase";
	public static String myDirectory;
	
	public StoryFileHandler() {
	}
	
	/**
	 * use current time to create unique filenames
	 * @return String
	 */
	private String getTimestamp() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		// return the date as string
		return date;
	}
	

	/**
	 * create a filepath the current system time
	 * Call this from the activity
	 * @return
	 */
	public String createFilename(int mediaType) {
		
		String fileExtension = null;
		switch(mediaType) {
			case IMAGE_CAPTURE:
				fileExtension = ".jpg";
				break;
			case VIDEO_CAPTURE:
				fileExtension = ".mp4";
				break;
			case AUDIO_CAPTURE:
				fileExtension = ".mp3";
				break;
		}
		
		// get external storage folder
		File root = Environment.getExternalStorageDirectory();
		File myDirectory = new File(root.getAbsolutePath() + File.separator + MEDIA_DIRECTORY);
		// create directory
		myDirectory.mkdirs();
				
		String filename = "image_" + getTimestamp() + fileExtension;
		String filepath = myDirectory + File.separator + filename;  
		
		// return the filepath to use
		return filepath;
	}
	
	/**
	 * 
	 * Save an IMAGE to the SD card after 
	 * it's been generated
	 * 
	 * http://stackoverflow.com/questions/7887078/android-saving-file-to-external-storage
	 * 
	 * @param filename
	 * @param finalBitmap
	 */
	public void saveStoryImageToFile(String filename, Bitmap finalBitmap) {
		// create a new file
		File file = new File(myDirectory, filename);
		// delete it if it already exists
		if(file.exists()) file.delete();
		// save the Bitmap
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
