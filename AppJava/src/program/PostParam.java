package program;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
	
/*
 * Cette class g�re les param�tres depuis le client
 * si la m�thode est de type POST et enctype="multipart/form-data" (obligatoir)
 * */

public class PostParam {
	
	protected Map<String, String> map;

	public PostParam(char[] buffer){
		map = new Hashtable<String, String>(0);
		ArrayList<String> lines = getLines(buffer);
		getKeysValues(lines);
	}
	
	protected ArrayList<String> getLines(char[] buffer){
		ArrayList<String> result = new ArrayList<String>(0);
		String arrayChar = "";
		for(int i=0; i<buffer.length; i++){
			if(buffer[i] == '\r'){
				if(arrayChar.length() > 0){
					result.add(arrayChar);
				}
				arrayChar = "";
			}
			else{
				if(buffer[i] != '\n'){
					arrayChar += buffer[i];
				}
			}
		}
		return result;
	}
	
	protected void getKeysValues(ArrayList<String> lines){
		String line;
		String key;
		String value;
		String split2;
		for(int i=0; i<lines.size(); i++){
			line = lines.get(i);
			if(line.contains("Content-Disposition")){
				split2 = line.split("name")[1];
				key = split2.substring(2, split2.length() - 1);
				value = lines.get(i + 1);
				map.put(key, value);
			}
		}
		System.out.println("PostParam line => "+map+"\n");
	}
	
	public String get(String key){
		return map.get(key);
	}
	
}
