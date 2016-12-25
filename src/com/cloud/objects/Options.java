package com.cloud.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;

import com.cloud.gui.MusicHarvester;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * Options class is a static class for storing the saved options during runtime.
 * 
 * @author mcjcloud
 *
 */
public class Options {
	
	public static final String WIN_PATH = System.getProperty("user.home") + "/harvester_options.json";
	public static final String LIN_PATH = "~/harvester_options.json";
	public static final String DEFAULT = "{\"firstLaunch\":true,\"saveDirectory\":\"\"}";
	
	private static String json = "";
	
	private static boolean isFirstLaunch = false;
	private static String saveDirectory = "";
	
	// private constructor (to make the class non-initializable)
	private Options() { }

	// Getter and setter methods
	public static String getJson() {
		return json;
	}
	/**
	 * setJson parses the given json, attempts to assign the properties to Options' properties, and write the given json to file.
	 * 
	 * @param json - the json object as a string.
	 * @return true if the parse and assignment is successful, false otherwise.
	 */
	public static boolean setJson(String json) {
		// try to parse the json and assign the properties to variables.
		try {
			System.out.println("setting json: " + json);
			
			JsonObject object = Json.parse(json).asObject();
			boolean firstLaunch = object.get("firstLaunch").asBoolean();
			String saveDirectory = object.get("saveDirectory").asString();
			
			// get OS type
			String os = System.getProperty("os.name");
			System.out.println("os name: " + os);
			
			// write to file.
			File optionsFile = new File(os.toLowerCase().contains("windows") ? WIN_PATH : LIN_PATH);		// if it's a windows, use win path, else use linux path.
			System.out.println("json object.toString(): " + object.toString());
			
			// create the writer and write
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(optionsFile)));
			br.write(object.toString());
			br.flush();
			br.close();

			// assign the data
			Options.json = object.toString();
			Options.isFirstLaunch = firstLaunch;
			Options.saveDirectory = saveDirectory;
			
			// return true for success
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println();
			return false;
		}
	}
	
	// isFirstLaunch
	public static boolean isFirstLaunch() {
		return isFirstLaunch;
	}
	public static void setIsFirstLaunch(boolean isFirstLaunch) {
		// write new boolean to file (if it's different), and call setJson (which changes the properties)
		if(Options.isFirstLaunch != isFirstLaunch) {
			JsonObject object = Json.parse(Options.json).asObject();
			object.set("firstLaunch", isFirstLaunch);
			
			// call setJson (which will in turn update the properties of the class and write it to file)
			setJson(object.toString());
		}
	}
	
	// saveDirectory
	public static String getSaveDirectory() {
		return saveDirectory;
	}
	public static void setSaveDirectory(String saveDirectory) {
		// write new String to file (if it's different), and call setJson (which changes the properties)
		if(Options.saveDirectory != saveDirectory) {
			System.out.println("current options json: " + Options.json);
			JsonObject object = Json.parse(Options.json).asObject();
			object.set("saveDirectory", saveDirectory);
			
			// call setJson (which will in turn update the properties of the class and write it to file)
			setJson(object.toString());
		}
	}
}
