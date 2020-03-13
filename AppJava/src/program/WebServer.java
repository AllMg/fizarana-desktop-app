package program;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class WebServer implements Runnable {
	
	protected Socket socket;
	
	protected BufferedReader bufferedReader;
	protected PrintWriter printWriter;
	protected String line = null;
	protected int contentLength = 0;
	protected String strContentLength;
	protected boolean isGET = false;
	protected boolean isCSS = false;
	protected boolean isJS = false;
	protected boolean isPNG = false;
	protected boolean isICON = false;
	protected String cssName;
	protected String jsName;
	protected String pngName;
	protected PostParam postParam;
	
	WebServer(Socket socket){
		this.socket = socket;
		strContentLength = "";
	}

	@Override
	public void run() {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			readHeadersRequest();
			
			if(!isGET){ // si method POST
				if(contentLength > 0){
					char[] bufferPost = new char[contentLength];
					bufferedReader.read(bufferPost, 0, contentLength);
					postParam = new PostParam(bufferPost);
					executePostRequest();
				}
			}
			else{ // si method GET
				executeGetRequest();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			close();
		}
	}
	
	/*
	 * Lit les en-têtes de la requête
	 * on initialise ici les attributs utiles aux traitement
	 * */
	protected void readHeadersRequest() throws NumberFormatException, IOException{
		System.out.println("readHeadersRequest");
		while((line = bufferedReader.readLine()) != null){
			if(line.length() == 0){
				break;
			}
			else if(line.contains("GET")){
				isGET = true;
				if(line.contains(".css")){
					isCSS = true;
					if(line.contains("style.css")){
						cssName = "style.css";
					}
					else if(line.contains("bootstrap.css")){
						cssName = "bootstrap.css";
					}
					else{
						cssName = "bootstrap.min.css";
					}
				}
				else if(line.contains(".js")){
					isJS = true;
					if(line.contains("jquery.min")){
						jsName = "jquery.min.js";
					}
					else{
						jsName = "bootstrap.min.js";
					}
				}
				else if(line.contains(".png")){
					isPNG = true;
					if(line.contains("folder.png")){
						pngName = "folder.png";
					}
					else{
						pngName = "file.png";
					}
				}
				else if(line.contains("favicon.ico")){
					isICON = true;
				}
			}
			else if(line.contains("Content-Length")){
				if(!isGET){
					for(char c : line.toCharArray()){
						if(isNumeric(c)){
							strContentLength += String.valueOf(c);
						}
					}
					contentLength = Integer.valueOf(strContentLength);
					System.out.println("contentLength => "+contentLength);
				}
			}
			System.out.println("line request => "+line);
		}
		System.out.println("isGET => "+isGET+"\n");
	}
	
	protected void executePostRequest() throws IOException{
		System.out.println("executePostRequest");
		if(postParam.get("isFolder").compareTo("Y") == 0){
			createPageResponse(postParam.get("fullPath"));
		}
		else{
			sendFileData(postParam.get("fullPath"));
		}
	}
	
	protected void sendFileData(String fullPath) throws IOException{
		File file = new File(fullPath);
		printWriter = new PrintWriter(socket.getOutputStream());
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
		
		printWriter.println("HTTP/1.1 200 OK");
		printWriter.println("Server: Java HTTP Server from All : 1.0");
		printWriter.println("Date: " + new Date());
		printWriter.println("Content-type: application/force-download");
		printWriter.println("Content-Disposition: inline; filename="+file.getName().replace(" ", "_"));
		printWriter.println("Content-length: " + file.length());
		printWriter.println();
		printWriter.flush();
		
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		int endLine;
		byte[] bytes = new byte[512 * 1024 * 8]; // buffer de 512 Ko
		while((endLine = bufferedInputStream.read(bytes, 0, bytes.length)) > -1){
			bufferedOutputStream.write(bytes, 0, endLine);
		}
		bufferedOutputStream.flush();
		bufferedInputStream.close();
	}
	
	/*
	 * Méthode appelé si la requête est de type GET
	 * */
	protected void executeGetRequest() throws IOException{
		System.out.println("executeGetRequest");
		if(isCSS){ // si le requête est de récupere le fichier css
			InputStream inputStreamCss = new FileInputStream("resources" + File.separator + cssName);
			byte[] dataCss = readInputData(inputStreamCss);
			sendResponse("text/css", null, dataCss);
		}
		else if(isJS){ // si le requête est de récupere le fichier js
			InputStream inputStreamJs = new FileInputStream("resources" + File.separator + jsName);
			byte[] dataJs = readInputData(inputStreamJs);
			sendResponse("application/javascript", null, dataJs);
		}
		else if(isPNG){ // si le requête est de récupere le fichier png (icon)
			InputStream inputStreamPng = new FileInputStream("resources" + File.separator + pngName);
			byte[] dataPng = readInputData(inputStreamPng);
			sendResponse("image/png", null, dataPng);
		}
		else if(isICON){ // si le requête est de récupere le fichier icon
			InputStream inputStreamIco = new FileInputStream("resources" + File.separator + "favicon.ico");
			byte[] data = readInputData(inputStreamIco);
			sendResponse("image/x-icon", null, data);
		}
		else{ // autrement on retourne la page d'accueil (racine de dossier)
			createPageResponse(null);
		}
	}
	
	/*
	 * Méthode appelé si la requête n'est pas pour du CSS, JS, ...
	 * donc pour renvoyer la page web
	 * 'root' est le chemin du dossier (peut être null)
	 * */
	protected void createPageResponse(String root) throws IOException{
		File[] files = FolderUtil.getFolderList(root);
		InputStream inputStreamPage = new FileInputStream("resources"+File.separator+"page.html");
		InputStream inputStreamFolder = new FileInputStream("resources"+File.separator+"page-folder-component.html");
		InputStream inputStreamHistory = new FileInputStream("resources"+File.separator+"page-root-component.html");
		String page = readHtmlFile(inputStreamPage);
		String folderComponent = readHtmlFile(inputStreamFolder);
		String folderHistory = readHtmlFile(inputStreamHistory);
		PageCreator pageCreator = new PageCreator(page, folderComponent, folderHistory);
		pageCreator.createRootHistory(root);
		pageCreator.createFolderList(files);
		sendPageResponse(pageCreator.getPage());
		inputStreamPage.close();
		inputStreamHistory.close();
		inputStreamFolder.close();
	}
	
	/*
	 * Méthode appelé si la requête n'est pas pour du CSS, JS, ...
	 * donc pour renvoyer la page web
	 * */
	protected void sendPageResponse(String page) throws IOException{
		sendResponse("text/html", page.getBytes("UTF-8"), null);
	}
	
	/* 
	 * retourne le resultat vers le client 
	 * si data est null alors file sera utilisé, sinon data sera toujours prioritaire
	 * ne doit pas être utilisé pour renvoyer de gros données (mp3, mp4, ...)
	 * */
	protected void sendResponse(String contentType, byte[] pageBytes, byte[] data) throws IOException{
		System.out.println("sendResponse => "+contentType+"\n");
		printWriter = new PrintWriter(socket.getOutputStream());
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
		printWriter.println("HTTP/1.1 200 OK");
		printWriter.println("Server: Java HTTP Server from All : 1.0");
		printWriter.println("Date: " + new Date());
		printWriter.println("Content-type: "+contentType);
		if(data == null){
			printWriter.println("Content-length: " + pageBytes.length);
		}
		else{
			printWriter.println("Content-length: " + data.length);
		}
		printWriter.println();
		printWriter.flush();
		
		if(data == null){
			bufferedOutputStream.write(pageBytes, 0, pageBytes.length);
		}
		else{
			bufferedOutputStream.write(data, 0, data.length);
		}
		bufferedOutputStream.flush();
	}
	
	/*
	 * Lit les lignes contenu dans le fichier utilisé pour avoir le corps de page
	 * les lignes sont assemblées pour faciliter le traitement
	 * */
	protected String readHtmlFile(InputStream in) throws IOException{
		System.out.println("readHtmlFile => "+in.available());
		String result = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while((line = br.readLine()) != null){
			if(line.length() > 0){
				result += line.trim();
			}
		}
		return result;
	}
	
	/*
	 * lit le fichier à renvoyer au client (le fichier créé puis effacé après utilisation)
	 * ne doit pas être utilisé pour lire les données de gros fichier
	 * */
	byte[] readResponseData(File file) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[(int)file.length()];
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) {
				fileIn.close();
			}
		}
		return fileData;
	}
	
	/*
	 * Lit les données à partir d'un InputStream (en utilisant surtout Class.getResourceAsStream())
	 * spécialement pour renvoyé les fichiers qui sont utilies à la page (CSS, JS, ...)
	 * */
	byte[] readInputData(InputStream file) throws IOException {
		byte[] fileData = new byte[file.available()];
		try {
			file.read(fileData);
		} finally {
			if (file != null) {
				file.close();
			}
		}
		return fileData;
	}
	
	
	
	protected boolean isNumeric(char c){
		char[] numbers = {'1','2','3','4','5','6','7','8','9','0'};
		for(char cc : numbers){
			if(c == cc){
				return true;
			}
		}
		return false;
	}
	
	protected void close(){
		if(bufferedReader != null){
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(printWriter != null){
			printWriter.close();
		}
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
