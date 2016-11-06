package com.cloud.gui;

import javafx.stage.Modality;
import javafx.stage.Stage;

import com.cloud.objects.SearchResult;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

/**
 * Browswer is a class to show a webpage to the user. Used to open the videos in the application.
 * 
 * @author brayd
 *
 */
public class Browser {
	
	// UI components
	Stage browserStage;
	Scene browserScene;
	StackPane browserPane;
	WebView webView;
	
	public Browser(SearchResult result, boolean embed) {
		
		String url = embed ? ("https://www.youtube.com/embed/" + result.getVideoId() + "?autoplay=1") : ("https://www.youtube.com/watch?v=" + result.getVideoId() + "&autoplay=1");
		
		browserStage = new Stage();
		browserStage.setOnCloseRequest(e -> webView.getEngine().load(null));
		browserStage.initModality(Modality.APPLICATION_MODAL);
		browserStage.setTitle(result.getTitle());

		webView = new WebView();
		webView.getEngine().load(url);
		
		browserPane = new StackPane();
		browserPane.getChildren().add(webView);
		
		browserScene = new Scene(browserPane, 720, 480);
		
		browserStage.setScene(browserScene);
		browserStage.showAndWait();
	}
	
}
