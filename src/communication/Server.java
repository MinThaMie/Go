package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

import communication.ClientHandler.Keyword;
import game.*;

public class Server {
	public static final Integer DEFAULT_PORT = 2772;
	public static final String SERVER_WELCOME_MSG = "CHAT Welcome to our Server!" + '\n'
														+ "CHAT You can use the following keywords:" + '\n'
														+ "CHAT GO boardsize --> To start a game of Go on your chosen size" + '\n'
														+ "CHAT CHAT message --> Chat with the other people on the server" + '\n'
														+ "CHAT EXIT --> you leave the server" + '\n';
	public static void main(String[] args) {
        Server server = new Server();
        server.run();
  
    }
	
	private Integer port;
	private ServerSocket ssock;
    private LinkedList<ClientHandler> threads;
    private LinkedList<Game> games;
    private HashMap<String, Integer> gameLobby;
    private Map<Integer, Set<String>> dimMap;

	public Server() {
		while (ssock == null) {
	    	port = askPort();
	    	try {
	    		ssock = new ServerSocket(port);
		    } catch (IOException e) {
		        System.out.println("ERROR: could not create a Serversocket on port " + port + ", please try again");
		    }
		}
    	threads = new LinkedList<>();
    	games = new LinkedList<>();
    	gameLobby = new HashMap<>();
    	dimMap = new HashMap<>();
    }
	
	private Integer askPort() {
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("On which port do you want your server to run?");
        Integer chosenPort = DEFAULT_PORT;
        try {
        	chosenPort = Integer.parseInt(line.readLine());
		} catch (IOException e) {
        	System.out.println("There is an error: " + e + " the server will use the default port " + DEFAULT_PORT);
        } catch (NumberFormatException e) {
        	System.out.println("You did not provide a number, please try again");
        	try {
        		chosenPort = Integer.parseInt(line.readLine());
        	} catch (IOException f) {
            	System.out.println("There is no reader");
        	}
        }
		return chosenPort;
	}
	
	public void run() {
		try {
			InetAddress ipAdress = InetAddress.getLocalHost();
			System.out.println("The IP-address of this server is: " + ipAdress.getHostAddress());

		} catch (UnknownHostException e) {
			System.out.println("The IP=address could not be found!");
		}
    	System.out.println("The server is running on port: " + ssock.getLocalPort());
    	while (true) {
	    	try {
	    		Socket clientsock;
	    		clientsock = ssock.accept();
	    		System.out.println("I've found a client");
	    		ClientHandler t = new ClientHandler(this, clientsock);
	    		t.sendMessage(SERVER_WELCOME_MSG);
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
	
	public void addToGameLobby(String name, Integer dimention, ClientHandler t) {
		gameLobby.put(name, dimention);
		if (!dimMap.containsKey(dimention)) {
			dimMap.put(dimention, new HashSet<>());		
		}
		dimMap.get(dimention).add(name);
		t.sendMessage(Keyword.WAITING.toString());
		checkForPair();
	}
	
	public void removeFromGameLobby(String name) {
		dimMap.get(gameLobby.get(name)).remove(name);
		gameLobby.remove(name);
	}
	
	private void checkForPair() { 
		for (Integer i : dimMap.keySet()) {
			if (dimMap.get(i).size() >= 2) {
				String[] players = dimMap.get(i).toArray(new String[dimMap.size()]);
				Player p1 = new HumanPlayer(players[0], Stone.BLACK);
				Player p2 = new HumanPlayer(players[1], Stone.WHITE);
				Game game = new Game(p1, p2, i);
				ClientHandler t1 = null;
				ClientHandler t2 = null;
				for (ClientHandler t : threads) {
					if (t.getName().equals(players[0])) {
						t1 = t;
					} else if (t.getName().equals(players[1])) {
						t2 = t;
					}
				}
				game.start(); //TODO: Add this to a new thread
				games.add(game);
				t1.sendMessage(Keyword.READY + " " + "black" + " " + players[1] + " " + i);
				t2.sendMessage(Keyword.READY + " " + "white" + " " + players[0] + " " + i);
				removeFromGameLobby(players[0]);
				removeFromGameLobby(players[1]);
			}
		}
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
