package com.cloud.notification;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

/**
 * AlertBox is a GUI element to display some info to the user.
 * 
 * @author brayd
 *
 */
public class AlertBox {
	
	public static void display(String title, String message) {
		// create the stage (window) and setup
		Stage alertStage = new Stage();
		alertStage.initModality(Modality.APPLICATION_MODAL);
		alertStage.setResizable(false);
		
		// set the title to the title passed
		alertStage.setTitle(title);
		
		// create a label to display the message passed
		Label messageLabel = new Label(message);
		Button closeButton = new Button("Close");
		closeButton.setOnAction(e -> alertStage.close());
		
		// create a layout pane to hold the label
		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(messageLabel, closeButton);	// add the label to the layout
		
		// create a scene to show the nodes
		Scene alertScene = new Scene(layout, 300, 100);
		
		// set the scene and show the application
		alertStage.setScene(alertScene);
		alertStage.showAndWait();
	}
}
