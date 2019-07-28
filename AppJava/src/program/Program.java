package program;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.nio.charset.Charset;

public class Program {
	
	static ServerSocket serverSocket;

	public static void main(String[] args) {
		System.out.println("System file.encoding => "+System.getProperty("file.encoding"));
		/*
		 * On modifie l'encodage des caractères pour la lecture/ecriture de flux
		 * I/O (pour java.io)
		 * */
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
		System.out.println("System file.encoding => "+System.getProperty("file.encoding")+"\n");
		
		long rankStart = -999999999;
		try {
			serverSocket = new ServerSocket(9191);
			while(true){
				WebServer webServer = new WebServer(serverSocket.accept(), rankStart);
				(new Thread(webServer)).start();
				rankStart++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
