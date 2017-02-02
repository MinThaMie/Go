package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

import communication.ClientHandler.Keyword;
import game.*;
import player.NetworkPlayer;
import player.Player;

public class Server {
	public static final Integer DEFAULT_PORT = 2772;
	public static final Integer MAX_CONNECTIONS = 20;

	public static final String SERVER_WELCOME_MSG = "CHAT Welcome to our Server!" + '\n'
														+ "CHAT You can use the following keywords:" + '\n'
														+ "CHAT GO boardsize --> To start a game of Go on your chosen size" + '\n'
														+ "CHAT CHAT message --> Chat with the other people on the server" + '\n'
														+ "CHAT EXIT --> you leave the server";
	public static void main(String[] args) {
        Server server = new Server();
        server.run();
  
    }
	
	private Integer port;
	private ServerSocket ssock;
    private LinkedList<ClientHandler> threads;
    private HashMap<ClientHandler, Game> clientListGames;
    private HashMap<String, Integer> gameQueue;
    private Map<Integer, LinkedList<String>> dimMap;
    private Map<Game, Set<ClientHandler>> gameListClients;
    private Game game;
    
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
    	clientListGames = new HashMap<>();
    	gameQueue = new HashMap<>();
    	dimMap = new HashMap<>();
    	gameListClients = new HashMap<>();
    	game = null;
    }
	
	private Integer askPort() {
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        print("On which port do you want your server to run?");
        Integer chosenPort = DEFAULT_PORT;
        boolean correctPort = false;
        while (!correctPort) {
	        try {
	        	chosenPort = Integer.parseInt(line.readLine());
	        	correctPort = true;
			} catch (IOException e) {
	        	print("There is an error: " + e + " the server will use the default port " + DEFAULT_PORT);
	        	correctPort = true;
	        } catch (NumberFormatException e) {
	        	print("You did not provide a number, please try again");
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
    		if (threads.size() <= MAX_CONNECTIONS) { 
		    	try {
		    		Socket clientsock;
		    		clientsock = ssock.accept();
		    		ClientHandler t = new ClientHandler(this, clientsock);
		    		t.sendMessage(SERVER_WELCOME_MSG);
		    		addHandler(t);
		    		t.start(); 
		    	} catch	(IOException e) {
			        System.out.println("ERROR: clientsocket causing trouble");
			    }
    		} else {
    			System.out.println("CAUTION: The server is full!!!");
    		}
    	}
    }
	
	public void print(String message) {
        System.out.println(message);
    }
	
	public void addToGameQueue(Integer dimension, ClientHandler t) {
		gameQueue.put(t.getClientName(), dimension);
		if (!dimMap.containsKey(dimension)) {
			dimMap.put(dimension, new LinkedList<>());		
		}
		dimMap.get(dimension).add(t.getClientName());
		t.sendMessage(Keyword.WAITING.toString());
		int key = checkForPair();
		if (key >= 0) {
			startGame(key);
		}
	}
	
	public void removeFromGameQueue(String name) {
		dimMap.get(gameQueue.get(name)).remove(name);
		gameQueue.remove(name);
	}
	
	/**
	 * Is called after a person has added the queue to play a game to check whether they chose a boardsize that was 
	 * already chosen by someone else.
	 * @return the dimension of the board, that can be used to get both players to start the game.
	 */
	private int checkForPair() { 
		for (Integer i : dimMap.keySet()) {
			if (dimMap.get(i).size() >= 2) {
				return i;
			}
		}
		return -1;
	}

	
	/**
	 * If there is a pair with the same boardsize a game is started. 
	 * First the order becomes random to determine who is white and who is black.
	 * The game is added to the gameList and they are removed from the queue
	 * @param key
	 */
	private void startGame(int key) {
		LinkedList<String> players = dimMap.get(key);
		Collections.shuffle(players);
		Player p1 = new NetworkPlayer(players.get(0), Stone.BLACK);
		Player p2 = new NetworkPlayer(players.get(1), Stone.WHITE);
		game = new Game(p1, p2, key);
		ClientHandler t1 = null;
		ClientHandler t2 = null;
		for (ClientHandler t : threads) {
			if (t.getClientName().equals(players.get(0))) {
				t1 = t;
			} else if (t.getClientName().equals(players.get(1))) {
				t2 = t;
			}
		}
		t1.sendMessage(Keyword.READY + " " + "black" + " " + players.get(1) + " " + key);
		t2.sendMessage(Keyword.READY + " " + "white" + " " + players.get(0) + " " + key);
		addGame(game, t1, t2);
		game.start(); 
		removeFromGameQueue(players.get(1));
		removeFromGameQueue(players.get(0));
	}
    
    /**
     * Sends a message using the collection of connected ClientHandlers
     * to all connected Clients.
     */
    public synchronized void broadcast(String msg) {
        for (ClientHandler handler : threads) {
        	handler.sendMessage(msg);
        }
    }
    /**
     * Some server messages consider only the people in the game, these are send using this function.
     */
    public synchronized void broadcastInGame(ClientHandler handler, String msg) {
        for (ClientHandler h : gameListClients.get(clientListGames.get(handler))) {
        	h.sendMessage(msg);
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
     * If a game is started by the server it's added to both gamelists.
     */
    public void addGame(Game g, ClientHandler t1, ClientHandler t2) {
    	clientListGames.put(t1, g);
    	clientListGames.put(t2, g);
    	if (!gameListClients.containsKey(g)) {
    		gameListClients.put(g, new HashSet<>());		
		}
    	gameListClients.get(g).add(t1);
    	gameListClients.get(g).add(t2);

    }
    
    public Game getGame(ClientHandler t1) {
    	return clientListGames.get(t1);
    }
    
    /**
     * Remove a game from the collection of Games on the server. 
     * @param handler ClientHandler that will be removed
     */
    public void removeGame(ClientHandler t1) {
    	gameListClients.get(gameQueue.get(t1)).remove(t1);
    	clientListGames.remove(t1);
    }
    
    /**
     * Remove a ClientHandler from the collection of ClientHanlders. 
     * @param handler ClientHandler that will be removed
     */
    public void removeHandler(ClientHandler handler) {
        threads.remove(handler);
    }
    /**
     * Kicks a handler if it does something invalid.
     */
    public void kick(ClientHandler handler) {
		handler.shutdown();
    	removeHandler(handler);
    }

}
