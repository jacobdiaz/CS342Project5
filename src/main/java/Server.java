import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	public class TheServer extends Thread{
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("______ SERVER ______");
			
		    while(true) {
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}


		class ClientThread extends Thread{
			// We want a method for each client to get the list of all clients on the server
			// In order to do this the server must send OUT data to the client
			// Then the client must get IN the data and display it to the GUI
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}

			public void updateClients(String message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}

			public void updateClientList(DataPackage data){
				try {
				String clientsOnServer = "";
				ClientThread thisClient = clients.get(count-1);
				System.out.println("Client#" + count);

				for (int i = 0; i < clients.size() ; i++) {
						ClientThread t = clients.get(i);
						clientsOnServer += t.count + " "; // Concatonate to the clientsOnServer
					}
							// Set the DataPackage to server data and send DataPackage out to client
							data.setData(clientsOnServer);
							thisClient.out.writeObject(data);
							System.out.println("DataPackage Type:\t"+data.getType()+"\nDataPackage Message: \t"+data.getData()+"\n"); // Send dataPackage back to client
						}catch (Exception e){e.printStackTrace();}
			}

			public void run(){
				int flag = 1;
				String data;
				DataPackage dataPackage;
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}

				updateClients("new client on server: client #"+count);

				 while(true) {
					    try {
							data = in.readObject().toString();
							dataPackage = new DataPackage(); // Instantiate a new datapackage
							dataPackage = (DataPackage)in.readObject(); // Case to datapackage
							String dataType = dataPackage.getType();
							System.out.println("______ New Data Package ______");

								updateClientList(dataPackage);
								//print a message to everyone
								callback.accept("Client:" + count + "send: "+ data);
								updateClients("client #"+count+" said "+ data);

					    	}
					    catch(Exception e) {
							callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	updateClients("Client #"+count+" has left the server!");
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
		}//end of client thread
}


	
	

	
