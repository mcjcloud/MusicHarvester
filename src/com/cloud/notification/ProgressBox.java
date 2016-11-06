package com.cloud.notification;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressBox {
	
	private static ProgressBar progBar;
	private static Stage progStage;
	
	public static void display(Task<Void> task, String songTitle) {
		progStage = new Stage();
		progStage.initModality(Modality.APPLICATION_MODAL);
		progStage.setResizable(false);
		// if the window is closed, cancel the task.
		progStage.setOnCloseRequest(e -> task.cancel(true));
		
		VBox progPane = new VBox(5);
		progPane.setAlignment(Pos.CENTER);
		
		// Label for the file downloading.
		Label songLabel = new Label();
		songLabel.setText(songTitle);
		
		// the progress bar that shows how far along the song is in its download
		progBar = new ProgressBar();
		progBar.progressProperty().unbind();
		progBar.progressProperty().bind(task.progressProperty());
		
		// shows how far along the song is in its download as a fraction of kb downloaded over total to download.
		Label progLabel = new Label();
		progLabel.textProperty().unbind();
		progLabel.textProperty().bind(task.messageProperty());
		
		// cancel button
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			task.cancel(true);
			progStage.close();
		});
		
		// add the components to the display
		progPane.getChildren().addAll(songLabel, progBar, progLabel, cancelButton);
			
		// creates a scene to add to the window and adds it.
		Scene progScene = new Scene(progPane, 300, 100);
		progStage.setScene(progScene);
		progStage.show();
	}
	
	public static void close() {
		System.out.println("entered close method");
		if(progStage != null) {
			System.out.println("progStage != null");
			progStage.hide();
			System.out.println("post hide in method");
		}
		else {
			System.out.println("progStage == null");
		}
	}

}
