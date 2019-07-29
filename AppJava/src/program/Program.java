package program;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.nio.charset.Charset;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Program extends Application {
	
	static ServerSocket serverSocket;

	@Override
	public void start(Stage stage) throws Exception {
		GridPane gridPane = new GridPane();
		gridPane.setMinSize(550, 400);
		gridPane.setVgap(15); 
	    gridPane.setHgap(15);  
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setStyle("-fx-background-color: #242442;");
		
		Text textIP = new Text("IP");
		Text textPort = new Text("Port");
		textIP.setFill(Color.WHITE);
		textIP.setStyle("-fx-font-size: 17px;-fx-font-weight: bold;");
		textPort.setStyle("-fx-font: normal bold 20px 'serif'; -fx-text-fill: white;"); 
		
		TextField textFieldIP = new TextField();
		TextField textFieldPort = new TextField();
		
		gridPane.add(textIP, 0, 0);
	    gridPane.add(textPort, 0, 1);
	    gridPane.add(textFieldIP, 1, 0);
	    gridPane.add(textFieldPort, 1, 1);
		
		Scene scene = new Scene(gridPane);
	       
		stage.setTitle("Fizarana"); 
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		if(serverSocket != null && !serverSocket.isClosed()){
			serverSocket.close();
		}
	}
	
	static void initTextEncoding(){
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
	}

	public static void main(String[] args) {
		initTextEncoding();
		
		long rankStart = -999999999;
		try {
			serverSocket = new ServerSocket(9191);
			
			Application.launch(args);
			
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
