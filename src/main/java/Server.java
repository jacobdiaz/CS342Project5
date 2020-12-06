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
		    System.out.println("Server is waiting for a client!");
			
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

			// TODO to create a group chat of clients do clients.get() all the selected groups
			// TODO We will add clients to a some list data structure and loop thropugh the list sending a message to all the clients
			// Writes a message to all clients
			public void updateClients(String message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}


			public void updateClientList(){
					System.out.println("in updateClientList()");
					// Get all the clients from the list
					for (int i = 0; i < clients.size() ; i++) {
						System.out.println("Client #" + i);
					}
			}

			public void run(){
				int flag = 1;
				String message;

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
//					    	String data = in.readObject().toString();
//							System.out.println("Data"+data);
//					    	callback.accept("client: " + count + " sent: " + data);
//					    	updateClients("client #"+count+" said: "+data);
							String data = in.readObject().toString();
							System.out.println("Incoming data:"+ data);

							if(data.equals("VIEW")){
								updateClientList();
							}
							else{
								//print a message to everyone
								callback.accept("Client:" + count + "send: "+ data);
								updateClients("client #"+count+" said "+ data);
							}
					    	}
					    catch(Exception e) {
//					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
//					    	updateClients("Client #"+count+" has left the server!");
//					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
		}//end of client thread
}


	
	

	
