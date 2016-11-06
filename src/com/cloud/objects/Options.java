package com.cloud.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * Options class is a static class for storing the saved options during runtime.
 * 
 * @author mcjcloud
 *
 */
public class Options {
	
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
			
			// write to file.
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("src/data/options.json"))));
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
