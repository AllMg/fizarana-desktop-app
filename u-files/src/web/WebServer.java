package web;

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
import java.net.SocketException;
import java.util.Date;

import program.PageCreator;
import utils.AllUtil;
import utils.FolderUtil;

public class WebServer implements Runnable {

	protected final String RES_FOLDER = "resources";
	protected final String CSS_FOLDER = RES_FOLDER + File.separator + "css" + File.separator;
	protected final String JS_FOLDER = RES_FOLDER + File.separator + "js" + File.separator;
	protected final String PNG_FOLDER = RES_FOLDER + File.separator + "png" + File.separator;
	protected final String HTML_FOLDER = RES_FOLDER + File.separator + "html" + File.separator;

	protected Socket socket;
	protected Session session;

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
	protected boolean requestHasRange = false;
	protected String cssName;
	protected String jsName;
	protected String pngName;
	protected PostParam postParam;

	public WebServer(Socket socket, Session ses) {
		this.socket = socket;
		this.session = ses;
		strContentLength = "";
	}

	@Override
	public void run() {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			SessionItem sessionItem = readHeadersRequest();

			if (!isGET) { // si method POST
				if (contentLength > 0) {
					char[] bufferPost = new char[contentLength];
					bufferedReader.read(bufferPost, 0, contentLength);
					postParam = new PostParam(bufferPost);
					executePostRequest(sessionItem);
					System.out.println(sessionItem);
				}
			} else { // si method GET
				SessionItem sessionItemIn = session.isIn(sessionItem);
				System.out.println(sessionItemIn);
				if (sessionItemIn != null) {
					sendFileData(sessionItemIn.getFullPath());
				} else {
					executeGetRequest();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/*
	 * Lit les en-tetes de la requete on initialise ici les attributs utiles aux
	 * traitement
	 */
	protected SessionItem readHeadersRequest() throws NumberFormatException, IOException {
		SessionItem sessionItem = new SessionItem();
		String boundary = "";
		while ((line = bufferedReader.readLine()) != null) {
			if (line.length() == 0) {
				break;
			} else if (line.contains("GET")) {
				isGET = true;
				if (line.contains(".css")) {
					isCSS = true;
					cssName = FolderUtil.getCorrectFile(CSS_FOLDER, line);
				} else if (line.contains(".js")) {
					isJS = true;
					jsName = FolderUtil.getCorrectFile(JS_FOLDER, line);
				} else if (line.contains(".png")) {
					isPNG = true;
					pngName = FolderUtil.getCorrectFile(PNG_FOLDER, line);
				} else if (line.contains("favicon.ico")) {
					isICON = true;
				}
			} else if (line.contains("Content-Length")) {
				if (!isGET) {
					for (char c : line.toCharArray()) {
						if (AllUtil.isNumeric(c)) {
							strContentLength += String.valueOf(c);
						}
					}
					contentLength = Integer.valueOf(strContentLength);
				}
			} else if (line.contains("Range")) {
				requestHasRange = true;
			} else if (line.contains("Origin") || line.contains("origin")) {
				sessionItem.setOrigin(line.substring(8));
			} else if ((line.startsWith("Content-Type") || line.startsWith("content-type"))
					&& line.contains("boundary")) {
				for (char c : line.toCharArray()) {
					if (AllUtil.isNumeric(c)) {
						boundary += String.valueOf(c);
					}
				}
				sessionItem.setBoundary(boundary);
			}
			System.out.println(line);
		}
		return sessionItem;
	}

	protected void executePostRequest(SessionItem sessionItem) throws IOException {
		if (postParam.get("isFolder").compareTo("Y") == 0) {
			createPageResponse(postParam.get("fullPath"));
		} else {
			sessionItem.setFullPath(postParam.get("fullPath"));
			session.items.add(sessionItem);
			sendFileData(postParam.get("fullPath"));
		}
	}

	protected void sendFileData(String fullPath) throws IOException {
		BufferedOutputStream bufferedOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			File file = new File(fullPath);
			printWriter = new PrintWriter(socket.getOutputStream());
			bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());

			printWriter.println("HTTP/1.1 200 OK");
			printWriter.println("Server: Java HTTP Server from All : 1.0");
			printWriter.println("Date: " + new Date());
			printWriter.println("Content-type: application/force-download");
			printWriter.println("Accept-Ranges: bytes");
			printWriter.println("Content-Disposition: inline; filename=" + file.getName().replace(" ", "_"));
			printWriter.println("Content-length: " + file.length());
			printWriter.println();
			printWriter.flush();

			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			int endLine;
			byte[] bytes = new byte[512 * 1024 * 8]; // buffer de 512 Ko
			while ((endLine = bufferedInputStream.read(bytes, 0, bytes.length)) > -1) {
				bufferedOutputStream.write(bytes, 0, endLine);
			}
			bufferedOutputStream.flush();
		} catch (SocketException se) {
			se.printStackTrace();
		} finally {
			if (bufferedOutputStream != null) {
				bufferedOutputStream.close();
			}
			if (bufferedInputStream != null) {
				bufferedInputStream.close();
			}
		}
	}

	/*
	 * Methode appele si la requete est de type GET
	 */
	protected void executeGetRequest() throws IOException {
		if (isCSS) { // si le requete est de recupere le fichier css
			InputStream inputStreamCss = new FileInputStream(CSS_FOLDER + cssName);
			byte[] dataCss = readInputData(inputStreamCss);
			sendResponse("text/css", dataCss);
		} else if (isJS) { // si le requete est de recupere le fichier js
			InputStream inputStreamJs = new FileInputStream(JS_FOLDER + jsName);
			byte[] dataJs = readInputData(inputStreamJs);
			sendResponse("application/javascript", dataJs);
		} else if (isPNG) { // si le requete est de recupere le fichier png (icon)
			InputStream inputStreamPng = new FileInputStream(PNG_FOLDER + pngName);
			byte[] dataPng = readInputData(inputStreamPng);
			sendResponse("image/png", dataPng);
		} else if (isICON) { // si le requete est de recupere le fichier icon
			InputStream inputStreamIco = new FileInputStream(RES_FOLDER + File.separator + "favicon.ico");
			byte[] data = readInputData(inputStreamIco);
			sendResponse("image/x-icon", data);
		} else { // autrement on retourne la page d'accueil (racine de dossier)
			createPageResponse(null);
		}
	}

	/*
	 * Methode appele si la requete n'est pas pour du CSS, JS, ... donc pour
	 * renvoyer la page web 'root' est le chemin du dossier (peut etre null)
	 */
	protected void createPageResponse(String root) throws IOException {
		File[] files = FolderUtil.getFolderList(root);
		InputStream inputStreamPage = new FileInputStream(HTML_FOLDER + "page.html");
		InputStream inputStreamFolder = new FileInputStream(HTML_FOLDER + "page-folder-component.html");
		InputStream inputStreamHistory = new FileInputStream(HTML_FOLDER + "page-root-component.html");
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
	 * Methode appele si la requete n'est pas pour du CSS, JS, ... donc pour
	 * renvoyer la page web
	 */
	protected void sendPageResponse(String page) throws IOException {
		sendResponse("text/html", page.getBytes("UTF-8"));
	}

	/*
	 * retourne le resultat vers le client si data est null alors file sera utilise,
	 * sinon data sera toujours prioritaire ne doit pas etre utilise pour renvoyer
	 * de gros donnees (mp3, mp4, ...)
	 */
	protected void sendResponse(String contentType, byte[] data) throws IOException {
		printWriter = new PrintWriter(socket.getOutputStream());
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
		printWriter.println("HTTP/1.1 200 OK");
		printWriter.println("Server: Java HTTP Server from All : 1.0");
		printWriter.println("Date: " + new Date());
		printWriter.println("Content-type: " + contentType);
		printWriter.println("Content-length: " + data.length);
		printWriter.println();
		printWriter.flush();

		bufferedOutputStream.write(data, 0, data.length);
		bufferedOutputStream.flush();
	}

	/*
	 * Lit les lignes contenu dans le fichier utilise pour avoir le corps de page
	 * les lignes sont assemblees pour faciliter le traitement
	 */
	protected String readHtmlFile(InputStream in) throws IOException {
		String result = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				result += line.trim();
			}
		}
		return result;
	}

	/*
	 * lit le fichier a renvoyer au client (le fichier cree puis efface apres
	 * utilisation) ne doit pas etre utilise pour lire les donnees de gros fichier
	 */
	byte[] readResponseData(File file) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[(int) file.length()];
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
	 * Lit les donnees a partir d'un InputStream (en utilisant surtout
	 * Class.getResourceAsStream()) specialement pour renvoye les fichiers qui sont
	 * utilies a la page (CSS, JS, ...)
	 */
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

	protected void close() {
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (printWriter != null) {
			printWriter.close();
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
