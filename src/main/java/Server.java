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
		
			try(ServerSocket mysocket = new ServerSocket(5555)){
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
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}

			public void updateClients(String message) {
				DataPackage data = new DataPackage("MESSAGE", message);
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(data);
					}
					catch(Exception e) {}
				}
			}

			public void updateClientList(DataPackage data){
				try {
				String clientsOnServer = "";
				ClientThread thisClient = clients.get(count-1);

				for (int i = 0; i < clients.size() ; i++) {
						ClientThread t = clients.get(i);
						clientsOnServer += t.count + " "; // Concatonate to the clientsOnServer
					}
							// Set the DataPackage to server data and send DataPackage out to client
							data.setData(clientsOnServer);
							thisClient.out.writeObject(data);
							data.printDetails();
						}catch (Exception e){e.printStackTrace();}
			}

			public void sendToRecipients(DataPackage data){
				try{
					String recipients = data.getRecipients();
					String []listOfRecipients = recipients.split("\\W+"); // Get each client number from recipient text
					for (int i = 0; i < listOfRecipients.length; i++) {
						int r = Integer.parseInt(listOfRecipients[i]);
						ClientThread t = clients.get(r-1);
						t.out.writeObject(data);
					}
				}catch (Exception e){e.printStackTrace();}
			}


			public void run(){
				int flag = 1;
				DataPackage data;
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
							data = (DataPackage)in.readObject(); // Case to datapackage
							String dataType = data.getType();
							System.out.println("______ New Data Package ______");
								if(dataType.equals("LIST")) {
									data.printDetails();
									updateClientList(data);
								}
								if (dataType.equals("MESSAGE")) {
									data.printDetails();
									callback.accept("Client:" + count + "send: " + data.getData());
									updateClients("client #" + count + " said " + data.getData());
								}
								if(dataType.equals("DM")){
									callback.accept("(Grouped message) Client:" + count + "send: " + data.getData());
									data.printDmDetails();
									sendToRecipients(data);
								}
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


	
	

	
