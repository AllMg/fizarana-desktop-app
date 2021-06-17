package program;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;

import web.WebServer;

public class Program {

	static ServerSocket serverSocket;

	static void run() throws IOException {
		serverSocket = new ServerSocket(9898);
		System.out.println("Server runnig on " + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());
		while (true) {
			WebServer webServer;
			try {
				webServer = new WebServer(serverSocket.accept());
				(new Thread(webServer)).start();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	static void initTextEncoding() {
		/*
		 * On modifie l'encodage des caracteres pour la lecture/ecriture de flux I/O
		 * (pour java.io)
		 */
		System.setProperty("file.encoding", "UTF-8");
		Field charset;
		try {
			charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null, null);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		initTextEncoding();
		try {
			run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
