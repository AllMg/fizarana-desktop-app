package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import utils.AllUtil;

public class PostParamUpload {

	protected Map<String, String> map;

	public PostParamUpload(BufferedReader bufferedReader) throws IOException {
		map = new Hashtable<String, String>(0);
		ArrayList<String> lines = getLines(bufferedReader);
		getKeysValues(lines);
	}

	protected ArrayList<String> getLines(BufferedReader bufferedReader) throws IOException {
		ArrayList<String> result = new ArrayList<String>(0);
		String arrayChar = "";
		char[] single = new char[1];
		while (bufferedReader.read(single) > -1) {
			if (single[0] == '\r') {
				if (arrayChar.length() > 0) {
					result.add(arrayChar);
					if (arrayChar.contains("byteArray")) {
						break;
					}
				}
				arrayChar = "";
			} else {
				if (single[0] != '\n') {
					arrayChar += single[0];
				}
			}
		}
		return result;
	}

	protected void getKeysValues(ArrayList<String> lines) {
		String line;
		String key;
		String value;
		String split2;
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i);
			//System.out.println(line);
			if (line.contains("Content-Disposition")) {
				if (!line.contains("byteArray")) {
					split2 = line.split("name")[1];
					key = split2.substring(2, split2.length() - 1);
					value = lines.get(i + 1);
					map.put(key, value);
				}
			}
		}
	}

	public int getBytes(BufferedReader bufferedReader, byte[] bytes, long totalRead, long fileSize) throws IOException {
		char[] single = new char[1];
		String arrayChar = "";
		int nbLu = 0;
		while (bufferedReader.read(single) > -1) {
			if (single[0] == '\r') {
				if (arrayChar.length() > 0 && arrayChar != " ") {
					bytes[nbLu] = Byte.valueOf(arrayChar);
					arrayChar = "";
					nbLu++;
					totalRead++;
				}
				if (totalRead >= fileSize)
					break;
			} else if (single[0] != '\n') {
				if (single[0] == ',') {
					if (arrayChar.length() > 0) {
						bytes[nbLu] = Byte.valueOf(arrayChar);
						arrayChar = "";
						nbLu++;
						totalRead++;
					}
				} else {
					arrayChar += single[0];
				}
			}
			if (nbLu == bytes.length)
				break;
		}
		return nbLu;
	}

	public String get(String key) {
		return map.get(key);
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
