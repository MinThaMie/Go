package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
	public static final Integer DEFAULT_PORT = 2772;
	
	public static void main(String[] args) {
        Server server = new Server();
        server.run();
        
    }
	
	private Integer port;
	private ServerSocket ssock;
    private LinkedList<ClientHandler> threads;

	public Server() {
    	port = askPort();
    	try {
    		ssock = new ServerSocket(port);
	    } catch (IOException e) {
	        System.out.println("ERROR: could not create a Serversocket on port " + port);
	    }
    	threads = new LinkedList<>();
    }
	
	private Integer askPort() {
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("On which port do you want your server to run?");
        Integer chosenPort = DEFAULT_PORT;
        try {
        	chosenPort = Integer.parseInt(line.readLine());
        } catch (IOException e) {
        	System.out.println("There is no reader");
        } catch (NumberFormatException e) {
        	System.out.println("You did not provide a number");
        }
		return chosenPort;
	}
	
	//TODO: Check if it can print it's real IP-adress
	public void run() {
    	System.out.println("The server is running on port: " + ssock.getLocalPort());
    	while (true) {
	    	try {
	    		Socket clientsock;
	    		clientsock = ssock.accept();
	    		System.out.println("I've found a client");
	    		ClientHandler t = new ClientHandler(this, clientsock);
	    		addHandler(t);
	    		t.start();
	    		
	    	} catch	(IOException e) {
		        System.out.println("ERROR: clientsocket causing trouble");
		    }
    	}
    }
	
	public void print(String message) {
        System.out.println(message);
    }
    
    /**
     * Sends a message using the collection of connected ClientHandlers
     * to all connected Clients.
     * @param msg message that is send
     */
    public synchronized void broadcast(String msg) {
        for (ClientHandler handler : threads) {
        	handler.sendMessage(msg);
        }
    }
    
    /**
     * Add a ClientHandler to the collection of ClientHandlers.
     * @param handler ClientHandler that will be added
     */
    public void addHandler(ClientHandler handler) {
    	threads.add(handler);
    }
    
    /**
     * Remove a ClientHandler from the collection of ClientHanlders. 
     * @param handler ClientHandler that will be removed
     */
    public void removeHandler(ClientHandler handler) {
        threads.remove(handler);
    }

}
