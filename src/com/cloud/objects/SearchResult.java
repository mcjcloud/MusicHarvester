package com.cloud.objects;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javafx.scene.image.Image;

/**
 * SearchResult is an object to store a search result from the YouTube API. It stores a link, title, channel, length, image
 * 
 * @author mcjcloud
 */
public class SearchResult
{
	// private properties
	private URL link;
	private String videoId;
	private String title;
	private String channel;
	private Image image;
	
	/**
	 * Constructor takes the following parameters and constructs a new search result object 
	 * 
	 * @param urlPath
	 * @param title
	 * @param channel
	 * @param length
	 * @param imagePath
	 */
	public SearchResult(String videoId, String title, String channel, String imagePath)
	{
		try 
		{
			this.link = new URL("https://www.youtube.com/watch?v=" + videoId);
			this.image = new Image(imagePath);
		} 
		catch (MalformedURLException e) 
		{
			try {
				this.link = new URL("localhost");
			} catch (MalformedURLException e1) { System.out.println("LINK NOT FOUND"); }
			//return;
		} 
		catch (IllegalArgumentException ioe) 
		{
			this.image = new Image("res/x-img.png"); 
			//return;
		}
		this.title = title;
		this.videoId = videoId;
		this.channel = channel;
	}
	
	/*
	 * GETTERS and SETTERS
	 */
	/**
	 * getLink returns the Link as a URL object
	 * @return the URL associated with this SearchResult
	 */
	public URL getLink()
	{
		return this.link;
	}
	/**
	 * setLink to given URL path, may fail with IOException
	 * 
	 * @param urlPath
	 * @return a boolean: true if the assignment was successful, and false if it fails.
	 */
	public void setLink(URL url)
	{
		this.link = url;
	}
	
	/**
	 * get the videoId
	 * 
	 * @return the videoId as a String
	 */
	public String getVideoId()
	{
		return this.videoId;
	}
	
	/**
	 * getTitle returns the title of the SearchResult as a String
	 * 
	 * @return the video's title as a String
	 */
	public String getTitle()
	{
		return this.title;
	}
	/**
	 * setTitle sets the title for the associated SearchResult
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * set the name of the YouTube channel for this SearchResult
	 * 
	 * @return
	 */
	public String getChannelName() 
	{
		return this.channel;
	}
	/**
	 * set the channel name with a String
	 * 
	 * @param channel - the new Channel name
	 */
	public void setChannelName(String channel) 
	{
		this.channel = channel;
	}

	/**
	 * get the ImageIcon representing the video
	 * 
	 * @return
	 */
	public Image getImage() 
	{
		return this.image;
	}
	/**
	 * set the image
	 * 
	 * @param image is an ImageIcon
	 */
	public void setImage(Image image) 
	{
		this.image = image;
	}
}
