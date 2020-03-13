package program;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Program extends Application {
	
	static ServerSocket serverSocket;

	@Override
	public void start(Stage stage) throws Exception {
		GridPane gridPane = new GridPane();
		gridPane.setMinSize(550, 300);
		gridPane.setVgap(15); 
	    gridPane.setHgap(15);  
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setStyle("-fx-background-color: #242442;");
		//gridPane.setGridLinesVisible(true);
		
		GridPane gridPaneField = new GridPane();
		gridPaneField.setVgap(15); 
		gridPaneField.setHgap(15);  
		gridPaneField.setAlignment(Pos.CENTER);
		//gridPaneField.setGridLinesVisible(true);
		
		Text textIP = new Text("IP");
		Text textPort = new Text("Port");
		textIP.setFill(Color.valueOf("aliceblue"));
		textIP.setStyle("-fx-font-size: 17px;-fx-font-weight: bold;");
		textPort.setFill(textIP.getFill());
		textPort.setStyle(textIP.getStyle());
		
		TextField textFieldIP = new TextField(InetAddress.getLocalHost().getHostAddress());
		textFieldIP.setEditable(false);
		textFieldIP.setFocusTraversable(false);
		TextField textFieldPort = new TextField("9898");
		
		Button button = new Button("Je valide");
		button.setTextFill(Color.valueOf("aliceblue"));
		button.setStyle("-fx-background-color: #2d2d52;-fx-font-size: 16px;");
	    GridPane.setHalignment(button, HPos.RIGHT);
	    
	    StackPane stackPane = new StackPane();
	    stackPane.setMinSize(300, 100);
	    stackPane.setVisible(false);
	    
	    Label labelError = new Label("Impossible de lancer le partage");
	    labelError.setTextFill(Color.valueOf("#ff0000"));
	    labelError.setVisible(false);
	    Label labelSuccess = new Label("Tapez IP:Port dans la bare d'adresse\nd'un navigateur pour voir");
	    labelSuccess.setTextFill(Color.valueOf("aliceblue"));
	    labelSuccess.setTextAlignment(TextAlignment.CENTER);
	    labelSuccess.setVisible(false);
		
	    gridPaneField.add(textIP, 0, 0);
	    gridPaneField.add(textPort, 0, 1);
	    gridPaneField.add(textFieldIP, 1, 0);
	    gridPaneField.add(textFieldPort, 1, 1);
	    gridPaneField.add(button, 0, 2, 2, 1);
	    
	    stackPane.getChildren().add(labelError);
	    stackPane.getChildren().add(labelSuccess);
	    
	    gridPane.add(gridPaneField, 0, 0);
	    gridPane.add(stackPane, 0, 1);
	    
	    button.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent actionEvent) {
				try {
					serverSocket = new ServerSocket(Integer.valueOf(textFieldPort.getText()));
					
					labelError.setVisible(false);
				    stackPane.setStyle("-fx-border-radius: 10px;-fx-border-color: aliceblue;-fx-font-size: 13px;");
				    stackPane.setVisible(true);
				    
				    String hostAddress = textFieldIP.getText();
				    hostAddress = hostAddress.compareTo("0.0.0.0") == 0 ? "127.0.0.1" : hostAddress;
				    labelSuccess.setText(labelSuccess.getText().replace("IP", hostAddress).replace("Port", String.valueOf(serverSocket.getLocalPort())));
				    labelSuccess.setVisible(true);
				    
				    textFieldPort.setEditable(false);
				    textFieldPort.setFocusTraversable(false);
				    
				    ((Button) actionEvent.getSource()).setVisible(false);
				    
				    new Thread(new Runnable(){

						@Override
						public void run() {
							while(true){
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
					}).start();

				} catch (IOException | NumberFormatException e) {
					e.printStackTrace();
				    labelSuccess.setVisible(false);
					stackPane.setStyle("-fx-border-radius: 10px;-fx-border-color: #ff0000;-fx-font-size: 13px;");
					stackPane.setVisible(true);
					labelError.setVisible(true);
				}
			}
			
	    });
		
		Scene scene = new Scene(gridPane);
	       
		stage.setTitle("Fizarana"); 
		stage.setScene(scene);
		stage.setResizable(false);
		stage.getIcons().add(new Image("file:resources/icon.png"));
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
		
		Application.launch(args);
	}

}
