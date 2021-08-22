package mouserecmacro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.prefs.Preferences;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.json.JSONArray;

public class Configurations
{
	Preferences preferences;
	
	private static int MACRO_KEY = NativeKeyEvent.VC_R;
	private static int MACRO_TOTAL_TIME = 1000;
	
	private static JSONArray MOUSE_POSITIONS_JSON = new JSONArray(); 
	
	public Configurations() {
		this.preferences = Preferences.userNodeForPackage(Program.class);
		this.reload();
	}
	
	public void reload() {
		MACRO_KEY = this.preferences.getInt("MACRO_KEY", NativeKeyEvent.VC_R);
		MACRO_TOTAL_TIME = this.preferences.getInt("MACRO_TOTAL_TIME", 1000);
		MOUSE_POSITIONS_JSON = new JSONArray(this.preferences.get("MACRO_POSITIONS_BUTTON", "[]"));
	}
	
	public void save() {
		this.preferences.putInt("MACRO_KEY", MACRO_KEY);
		this.preferences.putInt("MACRO_TOTAL_TIME", MACRO_TOTAL_TIME);
		this.preferences.put("MACRO_POSITIONS_BUTTON", MOUSE_POSITIONS_JSON.toString());
	}
	
	public int getMacroKey() {
		return MACRO_KEY;
	}
	
	public void setMacroKey(int keyCode) {
		MACRO_KEY = keyCode;
	}
	
	public int getMacroTotalTime() {
		return MACRO_TOTAL_TIME;
	}
	
	public void setMacroTotalTime(int miliSeconds) {
		MACRO_TOTAL_TIME = miliSeconds;
	}
	
	public void setNewMousePositionsButton(ArrayList<Integer> mousePositionsButton,  ArrayList<HashMap<String, Integer>> mousePositions) {
		MOUSE_POSITIONS_JSON = convertMousePositionsToJsonArray(mousePositionsButton, mousePositions);
		this.save();
	}
	
	public void clearMousePositions() {
		MOUSE_POSITIONS_JSON.clear();
	}
	
	public ArrayList<String> getConvertedMousePositions() {
		return convertMousePositionsToArrayList(MOUSE_POSITIONS_JSON);
	}
	
	public ArrayList<String> convertMousePositionsToArrayList(JSONArray mousePositionsJsonArray) {
		ArrayList<String> mousePositionsArrayList = new ArrayList<String>();
		
		Iterator<Object> mouseConvertedPositionsIterator = mousePositionsJsonArray.iterator();
		while(mouseConvertedPositionsIterator.hasNext()) {
			String mousePositionString = (String) mouseConvertedPositionsIterator.next();
			mousePositionsArrayList.add(mousePositionString);
		}
		
		return mousePositionsArrayList;
	}
	
	public JSONArray convertMousePositionsToJsonArray(ArrayList<Integer> mousePositionsButton, ArrayList<HashMap<String, Integer>> mousePositions) {
		JSONArray mousePositionsJson = new JSONArray();
		Iterator<Integer> macroPositionsButtonArrayIterator = mousePositionsButton.iterator();
		
		int index = 0;
		while(macroPositionsButtonArrayIterator.hasNext()) {
			
			int macroPositionButton = macroPositionsButtonArrayIterator.next();
			HashMap<String, Integer> macroPositionMap = mousePositions.get(index);
			
			int macroPositionX = macroPositionMap.get("x");
			int macroPositionY = macroPositionMap.get("y");
			
			String positionToSaveString =
					String.valueOf(macroPositionButton) + ";" +
					String.valueOf(macroPositionX) + ";" + 
					String.valueOf(macroPositionY) + ";";
			
			mousePositionsJson.put(positionToSaveString);
			index++;
		}
		
		return mousePositionsJson;
	}
}
