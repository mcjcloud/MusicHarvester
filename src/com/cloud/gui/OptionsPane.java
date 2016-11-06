package com.cloud.gui;

import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

import com.cloud.objects.Options;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * OptionsPane is a GUI class to show the configurable options in the app
 * 
 * @author mcjcloud
 *
 */
public class OptionsPane {
	
	// private constructor
	private OptionsPane() { }
	
	public static void display() {
		// create stage
		Stage optionsStage = new Stage();
		optionsStage.initModality(Modality.APPLICATION_MODAL);
		optionsStage.setResizable(false);
		optionsStage.setTitle("Options");
		
		// VBox is the main parent layout
		VBox layout = new VBox(50);
		
		// create Save Directory option
		BorderPane pathOptionPane = new BorderPane();
		pathOptionPane.setPadding(new Insets(50.0));
		
		Label pathLabel = new Label("Save Directory");
		Button setPathButton = new Button("Browse");
		setPathButton.setOnAction(e -> {
			// TODO: bring up file chooser
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Choose Save Location");
			
			if(Options.getSaveDirectory() != null && !Options.getSaveDirectory().equals("") && (new File(Options.getSaveDirectory()).isDirectory())) {
				dirChooser.setInitialDirectory(new File(Options.getSaveDirectory()));
			}
			
			// select the folder and set it as the options' directory.
			File saveFile = dirChooser.showDialog(optionsStage);
			Options.setSaveDirectory(saveFile.getAbsolutePath());
		});
		pathOptionPane.setLeft(pathLabel);
		pathOptionPane.setRight(setPathButton);
		
		// put GUI together and show
		layout.getChildren().add(pathOptionPane);
		Scene optionsScene = new Scene(layout, 300, 300);
		optionsStage.setScene(optionsScene);
		optionsStage.showAndWait();
	}
}
