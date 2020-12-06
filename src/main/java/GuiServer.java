
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

public class GuiServer extends Application{

	TextField s1,s2,s3,s4, messageTextField;
	Button serverChoice,clientChoice, sendBtn;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	
	ListView<String> listItems, listItems2, listOfClients;
	Button viewClientsBtn;
	Label clientsLabel;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");
		
		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});
		});
											
		});

		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");
		
		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
											primaryStage.setTitle("Client");
											clientConnection = new Client(data->{
												DataPackage dp = (DataPackage) data; // Dp is sent from
												if (dp.getType().equals("LIST")){
													Platform.runLater(()->{
														clientsLabel.setStyle("-fx-text-fill: white;");
														clientsLabel.setText("Clients On Server: (Client Number) "+dp.getData().toString());
													});
												}
												if(dp.getType().equals("MESSAGE")) {
													Platform.runLater(() -> {
														System.out.println(dp.getData());
														listItems2.getItems().add(((DataPackage) data).getData().toString());
													});
												}
							});
											clientConnection.start();
		});

		// Start GUI
		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		startScene = new Scene(startPane, 800,800);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();

		// Client GUI
		messageTextField = new TextField();
		sendBtn = new Button("Send");
		sendBtn.setOnAction(e->{
			DataPackage dataPackage = new DataPackage("MESSAGE", messageTextField.getText());
			clientConnection.send(dataPackage);
			messageTextField.clear();
		});

		// View Clients btn
		viewClientsBtn = new Button("View Clients");
		viewClientsBtn.setOnAction(e->{
			DataPackage dataPackage = new DataPackage("LIST","");
			clientConnection.send(dataPackage);
		});
		clientsLabel = new Label();

		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(startScene);
		primaryStage.show();
	}

	public Scene createServerGui() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		pane.setCenter(listItems);
		return new Scene(pane, 500, 400);
	}
	
	public Scene createClientGui() {
		clientBox = new VBox(10, listItems2, messageTextField, sendBtn,viewClientsBtn, clientsLabel);
		clientBox.setStyle("-fx-background-color: blue");
		return new Scene(clientBox, 500, 600);
	}
}
