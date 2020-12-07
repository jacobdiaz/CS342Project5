
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

public class GuiServer extends Application{

	TextField s1,s2,s3,s4, messageTextField,directMessageTextField,recipientsTextField;
	Button serverChoice,clientChoice, sendToAllBtn;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox, directMessageRow;
	VBox clientBox, allMessageContainer, directMessageContainer, bottomContainer;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	
	ListView<String> listItems, listItems2, listOfClients;
	Button viewClientsBtn, sendDirectMessageBtn;
	Label clientsLabel, dmHeader, allMessageHeader, dmInfo, allMessageInfo, messageClientLabel ;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {


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
												if(dp.getType().equals("LIST")) {
													Platform.runLater(() -> {
														clientsLabel.setText("Clients On Server: (Client Number) " + dp.getData().toString());
													});
												}
												if(dp.getType().equals("MESSAGE")) {
													Platform.runLater(() -> {
														System.out.println(dp.getData());
														listItems2.getItems().add(((DataPackage) data).getData().toString());
													});
												}

												if(dp.getType().equals("DM")){
													Platform.runLater(()->{
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
		messageTextField.setPromptText("Message");
		sendToAllBtn = new Button("Send to all!");
		sendToAllBtn.getStyleClass().add("btn");
		sendToAllBtn.setOnAction(e->{
			DataPackage messagePackage = new DataPackage("MESSAGE", messageTextField.getText());
			clientConnection.send(messagePackage);
			messageTextField.clear();
		});

		// View Clients btn
		viewClientsBtn = new Button("View Clients");
		viewClientsBtn.getStyleClass().add("btn");
		viewClientsBtn.setOnAction(e->{
			DataPackage listPackage = new DataPackage("LIST");
			clientConnection.send(listPackage);
		});
		clientsLabel = new Label();
		clientsLabel.getStyleClass().add("p");
		// Direct Message Col
		directMessageContainer = new VBox(8);
		directMessageRow = new HBox(12);
		directMessageTextField = new TextField();
		recipientsTextField = new TextField();
		dmInfo = new Label("Direct messages allow you to send messages to multiple people or a single person!");
		dmInfo.setWrapText(true);
		dmInfo.setTextAlignment(TextAlignment.JUSTIFY);
		messageClientLabel = new Label("Message                                               Recipients (Enter List of Client#)");
		// Set Prompts
		directMessageTextField.setPromptText("Message");
		recipientsTextField.setPromptText("ex.) 1 2 3 4 5");
		sendDirectMessageBtn = new Button("Send Direct Message");

		dmHeader = new Label("Direct Message");
		dmHeader.getStyleClass().add("h1");

		allMessageHeader = new Label("General Message");
		allMessageHeader.getStyleClass().add("h1");


		allMessageInfo = new Label("General messages allow you to send messages to everyone!");
		allMessageInfo.setWrapText(true);
		allMessageInfo.setTextAlignment(TextAlignment.JUSTIFY);

		// Set Action
		sendDirectMessageBtn.setOnAction(e->{
			if(directMessageTextField != null && recipientsTextField.getText() != null) {
				DataPackage dmPackage = new DataPackage("DM", directMessageTextField.getText(), recipientsTextField.getText());
				clientConnection.send(dmPackage);
				directMessageTextField.clear();
				recipientsTextField.clear();
			}
		});
		sendDirectMessageBtn.getStyleClass().add("btn");

		directMessageRow.getChildren().addAll(directMessageTextField,recipientsTextField);
		directMessageContainer.getChildren().addAll(dmHeader,dmInfo,messageClientLabel,directMessageRow,sendDirectMessageBtn);
		directMessageContainer.setPadding(new Insets(10));
		directMessageContainer.getStyleClass().add("container");


		allMessageContainer = new VBox(10);
		allMessageContainer.setPadding(new Insets(10));
		allMessageContainer.getStyleClass().add("container");
		allMessageContainer.getChildren().addAll(allMessageHeader, allMessageInfo,messageTextField,sendToAllBtn);
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
		pane.setStyle("-fx-background-color: #ffffff");
		pane.setCenter(listItems);
		return new Scene(pane, 500, 400);
	}
	
	public Scene createClientGui() {
		clientBox = new VBox(8, listItems2,allMessageContainer, directMessageContainer,viewClientsBtn,clientsLabel);
		clientBox.setStyle("-fx-background-color: #eaebec");
		Scene clientScene = new Scene(clientBox, 600, 700);
		clientScene.getStylesheets().addAll("styles.css");
		return clientScene;
	}
}
