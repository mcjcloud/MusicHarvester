package com.cloud.gui;

import java.net.URL;

import com.cloud.objects.SearchResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * SearchResultsPane is a subclass of BorderPane. It is a GUI element to represent a search result from the YouTube API.
 * 
 * @author mcjcloud
 *
 */
public class SearchResultPane extends BorderPane {
	
	/*
	 * GUI Components
	 */
	Label thumbnailLabel;
	VBox infoPane;
		Label titleLabel;
		Label channelLabel;
		
	/*
	 * Data
	 */
	private SearchResult result;
	private URL link;
	private String videoId;
	private String title;
	private String channel;
	private Image image;
	
	public SearchResultPane(SearchResult result) {
		
		this.link = result.getLink();
		this.title = result.getTitle();
		this.videoId = result.getVideoId();
		this.channel = result.getChannelName();
		this.image = result.getImage();
		this.result = result;
		
		thumbnailLabel = new Label();
		thumbnailLabel.setGraphic(new ImageView(this.image));	// set the label as the result image
		setLeft(thumbnailLabel);
		
		infoPane = new VBox(3);
		infoPane.setAlignment(Pos.CENTER_LEFT);
		infoPane.setPadding(new Insets(5));
		titleLabel = new Label(this.title);
		titleLabel.setFont(new Font(17));	
		channelLabel = new Label(this.channel);
		infoPane.getChildren().addAll(titleLabel, channelLabel);
		setCenter(infoPane);
	}
	
	public SearchResult getSearchResult() {
		return this.result;
	}
}
