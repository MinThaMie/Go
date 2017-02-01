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
					Stone moveStone = null;
					if (server.getGame(this) != null) {
						HashMap<String, Player> players = server.getGame(this).getPlayers();
						moveStone = players.get(clientName).getColor();
    				}
					switch (keyword) {
		    			case EXIT : 
		    				server.broadcast(Keyword.WARNING + " " + clientName + " has left the server");
		    				sendMessage(Keyword.EXIT + "");
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
		    				Stone turnStone = stringToStone(color);
		    				if (turnStone.equals(moveStone)) {
			    				try {
				    				int x = Integer.parseInt(msgParts[1]);
				    				int y = Integer.parseInt(msgParts[2]);
				    				
				    				if (server.getGame(this).isAllowed(x, y, moveStone)) { 
				    					server.broadcastInGame(this, Keyword.VALID + " " + color +  " " + x + " " + y); 
				    					server.getGame(this).doMove(x, y, moveStone);
				    					server.getGame(this).setPasses(0);
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
		    			case PASS:
		    				server.getGame(this).setPasses(server.getGame(this).getPasses() + 1);
		    				server.broadcastInGame(this, Keyword.PASSED + " " + stoneToString(moveStone));
		    				if (server.getGame(this).getPasses() == 2) {
		    					server.getGame(this).calculateScore();
		    					int blackScore = server.getGame(this).getScore(Stone.BLACK);
		    					int whiteScore = server.getGame(this).getScore(Stone.WHITE);
		    					server.broadcastInGame(this, Keyword.END + " " + blackScore + " " + whiteScore);
		    				}
		    				break;
		    			case TABLEFLIP:
		    				server.broadcastInGame(this, Keyword.TABLEFLIPPED + " " + stoneToString(moveStone));
	    					server.broadcastInGame(this, Keyword.END + " -1 -1");
		    				break;
	    				default:
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
	
	private String stoneToString(Stone stone) {
		return stone == Stone.BLACK ? "black" : "white";
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
