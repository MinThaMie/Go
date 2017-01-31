package communication;

import java.io.*;
import java.net.Socket;

import game.Stone;

public class ClientHandler extends Thread {
	private Server server;
	private Socket clientSocket;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;

	public enum Keyword {
		PLAYER, EXIT, GO, WAITING, READY, MOVE, VALID, INVALID, PASS, PASSED, TABLEFLIP, TABLEFLIPPED, CHAT, WARNING, END, CANCEL
	}

	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		server = serverArg;
		clientSocket = sockArg;
		in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
	}

	public void announce() throws IOException {
		clientName = in.readLine().toLowerCase();
		server.broadcast(Keyword.PLAYER + " " + clientName);
	}

	public void run() {
		try {
        	announce();
		} catch (IOException e) {
			sendMessage(Keyword.WARNING + " Could not announce your presence!");
		}
    	String msg;
    	try {
			while ((msg = in.readLine()) != null) {
				server.print(msg); //To meet requirement 4 for the server, page 7
		
				String[] msgParts = msg.split(" ");
				Keyword keyword = Keyword.valueOf(msgParts[0]);
				switch (keyword) {
	    			case EXIT :
	    				server.broadcast(Keyword.WARNING + " " + clientName + " has left the server");
	    				shutdown();
	    				break;
	    			case GO:
	    				if (checkDimentions(msgParts[1])) {
	    					server.addToGameLobby(Integer.parseInt(msgParts[1]), this);
	    				} else {
	    					sendMessage(Keyword.WARNING + " You did not provide a valid boardsize: " +  msgParts[1]
	    							+ ". The boardsize must be odd and between 4 and 132. Please try again!");
	    				}
	    				break;
	    			case CANCEL: 
	    				server.removeFromGameLobby(clientName);
	    				sendMessage(Keyword.CHAT + " Server: you have been removed from the que!");
	    				break;
	    			case CHAT: 
	    				server.broadcast(msg);
	    				break;
	    			case MOVE:
	    				String color = server.getGame(this).getCurrentPlayer();
	    				try {
		    				int x = Integer.parseInt(msgParts[1]);
		    				int y = Integer.parseInt(msgParts[2]);
		    				
		    				if (server.getGame(this).isAllowed(x, y, stringToStone(color))) { 
		    					server.broadcastInGame(this, Keyword.VALID + " " + color +  " " + x + " " + y); 
		    				} else {
		    					server.broadcastInGame(this, Keyword.INVALID + " " + color + " You did something very wrong, you will be kicked");
		    					server.kick(this);
		    					server.broadcastInGame(this, Keyword.END + " -1 -1");
		    				}
	    				} catch (NumberFormatException e) {
	    					server.broadcastInGame(this, Keyword.INVALID + " " + color + " You did not provide a number as coordinate, you will be kicked");
	    					server.kick(this);
	    					server.broadcastInGame(this, Keyword.END + " -1 -1");
	    				}
	    				break;
    				default: 
        				sendMessage(Keyword.WARNING + " The server does not now this keyword");
        				break;
				}
			}
    	} catch (IOException e) {
    		sendMessage(Keyword.WARNING + " Could not read from Client");
    	}
    	shutdown();
	}

    private void shutdown() {
        server.removeHandler(this);
        server.broadcast("[" + clientName + " has left]");
        try {
        	in.close();
        	out.close();
        } catch (IOException e) {
    		e.printStackTrace();
    	} 
    }
    
    public String getClientName() {
    	return clientName;
    }
}
