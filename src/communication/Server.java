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
														+ "CHAT EXIT --> you leave the server";
	public static void main(String[] args) {
        Server server = new Server();
        server.run();
  
    }
	
	private Integer port;
	private ServerSocket ssock;
    private LinkedList<ClientHandler> threads;
    private HashMap<ClientHandler, Game> clientListGames;
    private HashMap<String, Integer> gameLobby;
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
    	gameLobby = new HashMap<>();
    	dimMap = new HashMap<>();
    	gameListClients = new HashMap<>();
    	game = null;
    }
	
	private Integer askPort() {
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        print("On which port do you want your server to run?");
        Integer chosenPort = DEFAULT_PORT;
        try {
        	chosenPort = Integer.parseInt(line.readLine());
		} catch (IOException e) {
        	print("There is an error: " + e + " the server will use the default port " + DEFAULT_PORT);
        } catch (NumberFormatException e) {
        	print("You did not provide a number, please try again");
        	try {
        		chosenPort = Integer.parseInt(line.readLine());
        	} catch (IOException f) {
            	print("There is no reader");
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
    		if (threads.size() <= 20) {
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
	
	public void addToGameLobby(Integer dimention, ClientHandler t) {
		gameLobby.put(t.getClientName(), dimention);
		if (!dimMap.containsKey(dimention)) {
			dimMap.put(dimention, new LinkedList<>());		
		}
		dimMap.get(dimention).add(t.getClientName());
		t.sendMessage(Keyword.WAITING.toString());
		int key = checkForPair();
		if (key >= 0) {
			startGame(key);
		}
	}
	
	public void removeFromGameLobby(String name) {
		dimMap.get(gameLobby.get(name)).remove(name);
		gameLobby.remove(name);
	}
	
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
		removeFromGameLobby(players.get(1));
		removeFromGameLobby(players.get(0));
	}
	
	private int checkForPair() { 
		for (Integer i : dimMap.keySet()) {
			if (dimMap.get(i).size() >= 2) {
				return i;
			}
		}
		return -1;
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
    
    public void removeGame(ClientHandler t1) {
    	gameListClients.get(gameLobby.get(t1)).remove(t1);
    	clientListGames.remove(t1);
    }
    
    /**
     * Remove a ClientHandler from the collection of ClientHanlders. 
     * @param handler ClientHandler that will be removed
     */
    public void removeHandler(ClientHandler handler) {
        threads.remove(handler);
    }
    
    public void kick(ClientHandler handler) {
    	clientListGames.get(handler).stopGame(); 
    	try {
    		handler.getClientSocket().close();
    	} catch (IOException e) {
    		print("The socket could not be closed");
    	}
    	removeHandler(handler);
    }

}
