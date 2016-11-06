package com.cloud.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cloud.notification.AlertBox;
import com.cloud.notification.ProgressBox;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Downloader is a static class that downloads files
 * 
 * @author mcjcloud
 */

public class Downloader
{
	private static final int BUFFER_SIZE = 4096;
	
	public static String manualURL;
	
	// cannon init
	private Downloader() {}

	/**
	 * downloads a file using a giving url and saves it to the given directory, returns true if successful and false if the download fails
	 * 
	 * @param fileURL
	 * @param filePath
	 * @return
	 */
	public static boolean mp3FromVideoLink(String fileURL, String directory, String fileName)
	{
		try 
		{
			// check file extension
			if(!fileName.endsWith(".mp3"))
				fileName += ".mp3";
			
			/*
			// check directory ending (to create proper file path later on
			if(!directory.endsWith("/") && !directory.endsWith("\\"))
				directory += "/";
			*/
			
			// append youtubeinmp3 url to fileURL
			fileURL = "http://www.youtubeinmp3.com/fetch/?video=" + fileURL; 
			manualURL = fileURL + "&title=" + fileName;
			System.out.println("file URL: " + fileURL);
			
			URL url = new URL(fileURL);
			System.out.println("Created URL");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			System.out.println("Opened Connection");
			int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				String contentType = connection.getContentType();
				long contentLength = connection.getContentLengthLong();
				
				System.out.println("Content-Type = " + contentType);
	            System.out.println("Content-Length = " + (contentLength / 1000) + "kb");
	            System.out.println("fileName = " + fileName);
	            
	            int timeout = 0;
	            while(!contentType.equals("audio/mpeg") && timeout < 100)
	            {
	            	connection = (HttpURLConnection) url.openConnection();
	            	contentType = connection.getContentType();
	            	contentLength = connection.getContentLengthLong();
	            	/* 
	        	   	// retry connection
		            if(!contentType.equals("audio/mpeg"))
		            {
		            	// send fail with download link
		            	AlertBox.display("Fail", "The file " + fileName + " failed to download.");
		            	return false;
		            }
		            */
	            	Thread.sleep(10);
	            	timeout += 1;
	            }
	            // checks if the while loop worked
	            if(!contentType.equals("audio/mpeg"))
	            {
	            	// send fail with download link
	            	AlertBox.display("Fail", "The file " + fileName + " failed to download.");
	            	return false;
	            }
	           

				// open input stream
				final InputStream inputStream = connection.getInputStream();
				System.out.println("Created inputStream");
				final String saveFilePath = directory + "/" + fileName;
				System.out.println("saveFilePath: " + saveFilePath);
				final long finContLength = contentLength;
				final String finFileName = fileName;

				// open output stream to save to
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);
				
				Task<Void> downloadTask = new Task<Void>() {
					@Override
					public Void call() {
						try {
							
							// read and write
							int bytesRead = -1;
							byte[] buffer = new byte[BUFFER_SIZE];
							
							// Prog bar
							int bytesTotal = 0;
							while((bytesRead = inputStream.read(buffer)) != -1)
							{
								outputStream.write(buffer, 0, bytesRead); // write to file
								// TODO: update progress; progress.set((double) bytesRead / (double) contentLength);
								updateProgress((double) bytesTotal, (double) finContLength);
								updateMessage((bytesTotal / 1000) + " Kb / " + (finContLength / 1000) + " Kb");	// convert bytes to kb
								bytesTotal += bytesRead;
								System.out.println("in while loop");
								
								// estimate catch
								//if((finContLength / 1000) - (bytesTotal / 1000) <= 2) break;
							}
							System.out.println("Exit while loop");
							Platform.runLater(() -> {
								ProgressBox.close();
								System.out.println("post close in Downloader.java");
								AlertBox.display("Success!", "The file " + finFileName + " downloaded successfully!");
							});
							
							outputStream.close();
							inputStream.close();
	        		   } catch(Exception e) {  }
	        		   
	        		   // required return statement
	        		   return null;
	        	   }
	           };
	           
	           // ProgressBar
	           downloadTask.setOnCancelled(e -> {
	        	   System.out.println("Cancelling download...");
	        	   // in case the exit button was pressed.
	        	   try {
	        		   inputStream.close();
	        		   outputStream.flush();
	        		   outputStream.close();
	        	   } 
	        	   catch (IOException e1) { 
	        		   System.out.println("Error closing streams."); 
	        	   }
	        	   finally {
	        		   // delete the file.
	        		   File badSong = new File(saveFilePath);
	        		   if(badSong.delete()) {
	        			   AlertBox.display("Download Cancelled", "The download was cancelled successfully.");
	        		   }
	        		   else {
	        			   AlertBox.display("Download Cancelled", "The download was canclled, but the file was not deleted.\nThe downloaded song may be corrupt.");
	        		   }
	        	   }
	           });
	           
	           // create the progress box.
	           ProgressBox.display(downloadTask, fileName + " is downloading...");
	           new Thread(downloadTask).start();
				
				manualURL = null;
				
				// alert box and return true
				return true;	// Success
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			AlertBox.display("Fail", "The file " + fileName + " failed to download.");
			return false;
		}
		System.out.println("returning false due to if statement I think");
		AlertBox.display("Fail", "The file " + fileName + " failed to download.");
		return false; // if one of the if statements in the try were false
	}
	
	public static String getJsonForURL(String urlPath) throws IOException
	{
		URL url = new URL(urlPath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int responseCode = connection.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_OK)
		{
			String contentType = connection.getContentType();
			int contentLength = connection.getContentLength();
			
			System.out.println("Content type: " + contentType);
			System.out.println("Content length: " + contentLength);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String result = "";
			
			String line = br.readLine();
			while(line != null)
			{
				result += line + "\n";
				line = br.readLine();
			}
			br.close();
			return result;
		}
		else
		{
			return "";
		}
	}
}
