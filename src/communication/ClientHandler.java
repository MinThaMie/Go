package communication;

import java.io.*;
import java.net.Socket;
import java.util.*;
import communication.ClientHandler.Keyword;
import game.*;


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
				//What my client sends to the clienthandler
				try {
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
		    				HashMap<String, Player> players = server.getGame(this).getPlayers();
		    				Stone moveStone = players.get(clientName).getColor();
		    				String color = server.getGame(this).getCurrentPlayer();
		    				Stone turnStone = stringToStone(color);
		    				System.out.println("I wanna move " + clientName +" " + moveStone.toString() + " turn: " + color + " " + turnStone.toString());
		    				if (turnStone.equals(moveStone)) {
			    				try {
				    				int x = Integer.parseInt(msgParts[1]);
				    				int y = Integer.parseInt(msgParts[2]);
				    				
				    				if (server.getGame(this).isAllowed(x, y, moveStone)) { 
				    					server.broadcastInGame(this, Keyword.VALID + " " + color +  " " + x + " " + y); 
				    					server.getGame(this).doMove(x, y, moveStone);
				    				} else {
				    					server.broadcastInGame(this, Keyword.INVALID + " " + color + " You did something very wrong, you will be kicked");
				    					server.kick(this);
				    					server.broadcastInGame(this, Keyword.END + " -1 -1");
				    				}
			    				} catch (NumberFormatException e) {
			    					server.broadcastInGame(this, Keyword.INVALID + " " + color + 
			    							" You did not provide a number as coordinate, you will be kicked");
			    					server.kick(this);
			    					server.broadcastInGame(this, Keyword.END + " -1 -1");
			    				}
		    				} else {
		    					server.broadcastInGame(this, Keyword.INVALID + " " + color + " You did something very wrong, you will be kicked");
		    					server.kick(this);
		    					server.broadcastInGame(this, Keyword.END + " -1 -1");
		    				}
	    					break;
					} 
				} catch	(IllegalArgumentException e) {
    				sendMessage(Keyword.WARNING + " The server does not now this keyword");
    			}
			}
    	} catch (IOException e) {
    		sendMessage(Keyword.WARNING + " Could not read from Client");
    	}
    	shutdown();
	}


	private Stone stringToStone(String color) {
		return color.equals("black") ? Stone.BLACK : Stone.WHITE;
	}

	public boolean checkDimentions(String dimention) {
		int dim = 0;
		try {
			dim = Integer.parseInt(dimention);
		} catch (NumberFormatException e) {
			return false;
		}
		return 5 <= dim && dim <= 131 && (dim % 2 != 0);
	}

	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	private void shutdown() {
		server.removeHandler(this);
		server.broadcast("[" + clientName + " has left]");
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getClientName() {
		return clientName;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
}
