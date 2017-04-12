package com.cloud.gui;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import com.cloud.connection.Downloader;
import com.cloud.notification.AlertBox;
import com.cloud.objects.Options;
import com.cloud.objects.SearchResult;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * MusicHarvester is the entry point class for the application, controls the main GUI
 * 
 * @author mcjcloud
 *
 */
public class MusicHarvester extends Application {
	
	// Please don't use my API key, then my app wouldn't be as useful :(
	public static final String API_KEY = "AIzaSyCpPbbi4daJdQC44SevOmM4-hSgz3vSOL4";
	final int NUM_RESULTS = 25;

	public static void main(String[] args) {
		
		// check options json
		// get OS type
		String os = System.getProperty("os.name");
		System.out.println("os name: " + os);
		
		// read file.
		File optionsFile = new File(Options.OPT_PATH);		// if it's a windows, use win path, else use linux path.
		
		System.out.println("read from: " + optionsFile.getAbsolutePath());
		if(optionsFile.isFile()) {
			try {
				System.out.println("is file!");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(optionsFile)));
				String options = "";
				String temp = br.readLine();
				while(temp != null) {
					options += temp;
					temp = br.readLine();
				}
				br.close();
				options = options.replaceAll("\\s", "").replaceAll("\n", "");										// replace all whitespace (I know I could use regex)
				System.out.println("read options: " + options);
				
				// parse json
				JsonObject json = Json.parse(options).asObject();
				System.out.println("json as string: " + json.toString());
				if(!Options.setJson(json.toString())) {
					AlertBox.display("Alert", "Could not load settings.");
				}
				
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
		// file does not exist, create it, and write to it.
		else {
			try {
				optionsFile.createNewFile();
				Options.setJson(Options.DEFAULT);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		// launch application.
		launch(args);
	}
	
	// GUI Components
	Scene appScene;
	
	// MENU BAR HEIRARCHY
	MenuBar menu;
		Menu fileMenu;
			MenuItem exitItem;
		Menu optionsMenu;
			MenuItem optionsItem;
	
	// HIERARCHY of GUI
	BorderPane appPane;
		// NORTH
		BorderPane searchPane;
			// WEST
			TextField searchField;
			// EAST
			Button searchButton;
		// CENTER
		ListView<SearchResultPane> resultsListView;
			SearchResultPane[] resultPanes;
		// EAST
		BorderPane actionPane;
			// NORTH
			TilePane buttonPane;
				Button downloadButton;
				Button playButton;
				Button playInYouTubeButton;
				Button playInBrowserButton;
	
	@Override
	public void start(Stage window) {
		window.setTitle("Music Harvester");
		
		// root layout
		appPane = new BorderPane();
		
		// layout menubar
		exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> System.exit(0));
		
		fileMenu = new Menu("File");
		fileMenu.getItems().add(exitItem);
		
		optionsItem = new MenuItem("Options");
		optionsItem.setOnAction(e -> OptionsPane.display());
		
		optionsMenu = new Menu("Options");
		optionsMenu.getItems().add(optionsItem);
		
		menu = new MenuBar();
		menu.getMenus().add(fileMenu);
		menu.getMenus().add(optionsMenu);
		

		searchPane = new BorderPane();
		searchPane.setTop(menu);
		
		// layout for the search bar and button
		searchField = new TextField();
		searchField.setMaxHeight(Double.MAX_VALUE);						// enable the searchfield to fill up the pane
		searchField.setPromptText("Search...");
		searchField.setOnAction(e -> searchButton.fire());				// when you hit enter in the searchfield, click the search button 
		searchPane.setCenter(searchField);
		
		searchButton = new Button("   Search   ");
		searchButton.setFont(new Font(15));
		searchButton.setOnAction(e -> query(searchField.getText()));	// on click: call query and to fill the results
		searchPane.setRight(searchButton);
		
		appPane.setTop(searchPane);										// add the search bar to the root pane
		
		
		// setup resultsScrollPane in the center
		resultsListView = new ListView<SearchResultPane>();
		resultsListView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		appPane.setCenter(resultsListView);
		
		// setup list of buttons
		actionPane = new BorderPane();
		buttonPane = new TilePane(Orientation.VERTICAL);
		buttonPane.setVgap(3);
		// buttons
		downloadButton = new Button("Download MP3");
		downloadButton.setFont(new Font(20));
		downloadButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);	// make buttons same size
		downloadButton.setOnAction(e -> {
			try {
				
				SearchResult result = resultsListView.getSelectionModel().getSelectedItem().getSearchResult();	// gets the search result of the currently selected result
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose Save Location");
				fileChooser.setInitialFileName(result.getTitle());
				
				if(Options.getSaveDirectory() != null && Options.getSaveDirectory() != "" && (new File(Options.getSaveDirectory()).isDirectory()))
					fileChooser.setInitialDirectory(new File(Options.getSaveDirectory()));
				
				fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", "*.mp3"));
				
				File saveFile = fileChooser.showSaveDialog(window);
				System.out.println("saveFile:" + saveFile);
				if(saveFile != null) {
					// start the download
					Downloader.mp3FromVideoLink(result.getLink().toString(), saveFile.getParent(), saveFile.getName());
				}
			} catch(Exception ex) { /* do nothing */ }
		});
		
		playButton = new Button("Play");
		playButton.setFont(new Font(20));
		playButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		playButton.setOnAction(e -> {
			try {
				SearchResult result = resultsListView.getSelectionModel().getSelectedItem().getSearchResult();	// gets the search result of the currently selected result
				new Browser(result, true);
			} catch(NullPointerException npe) { /* do nothing */ }
		});
		
		playInYouTubeButton = new Button("Play in YouTube");
		playInYouTubeButton.setFont(new Font(20));
		playInYouTubeButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		playInYouTubeButton.setOnAction(e -> {
			try {
				SearchResult result = resultsListView.getSelectionModel().getSelectedItem().getSearchResult();	// gets the search result of the currently selected result
				new Browser(result,  false);
			} catch(NullPointerException npe) { /* do nothing */ }
		});
		
		playInBrowserButton = new Button("Play in Browser");
		playInBrowserButton.setFont(new Font(20));
		playInBrowserButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		playInBrowserButton.setOnAction(e -> {
			// external browser
			SearchResult result = resultsListView.getSelectionModel().getSelectedItem().getSearchResult();	// gets the search result of the currently selected result
			if(Desktop.isDesktopSupported())
			{
				try 
				{
					Desktop.getDesktop().browse(new URI("http://www.youtube.com/watch?v=" + result.getVideoId() + "&autoplay=1"));
				} 
				catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
				catch(NullPointerException npe) { /* do nothing */ }
			} 
		});
		buttonPane.getChildren().addAll(downloadButton, playButton, playInYouTubeButton, playInBrowserButton);
		actionPane.setTop(buttonPane);
		appPane.setRight(actionPane);
		
		// set scene and show
		appScene = new Scene(appPane, 1000, 600);
		window.setScene(appScene);
		window.show();
		
		// check the firstLaunch and show welcome dialog.
		if(Options.isFirstLaunch()) {
			Alert thankyouAlert = new Alert(AlertType.INFORMATION);
			thankyouAlert.setTitle("Thank You!");
			thankyouAlert.setHeaderText("Thank you for using MusicHarvester!");
			thankyouAlert.setContentText("Thank you for using MusicHarvester. If you have questions and/or suggestions, email me at mcjcloud@outlook.com.");
			thankyouAlert.show();
			
			Options.setIsFirstLaunch(false);
		}
	}
	
	
	/**
	 * called by search button, query using YouTube API and fill out the search results
	 * 
	 * @param query - String; the query to make to YouTube
	 */
	private void query(String query) {
		// set the textfield to empty for new query
		searchField.setText("");
		
		// parse the query and format it for the URL
		String request = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=" + NUM_RESULTS + "&q=" + query.replaceAll(" ", "+") + "&key=" + API_KEY;	// url to request stuff in json format
		
		searchButton.setText("Loading...");
		searchButton.setDisable(true);
		searchField.setDisable(true);
		
		// clear results
		resultsListView.getItems().clear();
		
		new Thread(new Runnable() {
			
			public void run() {
				
				JsonObject json = null;
				try {
					json = Json.parse(Downloader.getJsonForURL(request)).asObject();
				} 
				catch (IOException e1) { System.out.println("JSON GET error"); }
				
				// get children for search
				JsonArray videoJsons = json.get("items").asArray();
				
				for(int i = 0; i < NUM_RESULTS; i++) {
					
					try {
						
						JsonObject videoObject = videoJsons.get(i).asObject();
						
						// get the values from the JSON object
						String videoId = videoObject.get("id").asObject().get("videoId").asString(); // gets the URL path
						String title = videoObject.get("snippet").asObject().get("title").asString();
						String channel = videoObject.get("snippet").asObject().get("channelTitle").asString();
						String imagePath = videoObject.get("snippet").asObject().get("thumbnails").asObject().get("default").asObject().get("url").asString();
						
						// add results to the ListView
						SearchResult searchResult = new SearchResult(videoId, title, channel, imagePath);
						Platform.runLater(new Runnable() {
							public void run() {
								resultsListView.getItems().add(new SearchResultPane(searchResult));
							}
						});
					}
					catch(Exception e) { System.out.println("JSON Error"); }
				}

				// re-enable button on the main thread
				Platform.runLater(new Runnable() {
					public void run() {
						searchButton.setDisable(false);
						//re-enable search bar
						searchField.setDisable(false);
						searchButton.setText("   Search   ");
					}
				});
			}
		}).start();
	}
}
