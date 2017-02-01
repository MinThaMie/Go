package communication;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.ClientHandler.Keyword;
import game.Game;
import game.NetworkPlayer;
import game.Player;
import game.Stone;


public class Client extends Thread {
	boolean started = true;

	public static void main(String[] args) {
		InetAddress host = null;
		int port = 0;
		while (host == null) {
			try {
				host = InetAddress.getByName(readString("To which ip-adress do you want to connect: ")); 
			} catch (UnknownHostException e) {
				print("ERROR: no valid hostname! Please try again.");
			}
		}
		while (port == 0) {
			try {
				port = Integer.parseInt(readString("To which port do you want to connect: ")); 
			} catch (NumberFormatException e) {
				print("ERROR: you did not send a number!");
			}
		}

		try {
			String myName = readString("Please tell me your name: ");
			Client client = new Client(myName, host, port);
			System.out.println("Im trying to connect to " + host + " and port " + port);
			client.sendMessage(Keyword.PLAYER + " " + myName);
			client.start();
			
			while (client.started) {
				String input = handleTerminalInput(client);
				client.sendMessage(input);
			} 
			
		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
			System.exit(0);
		}

	}
	
	
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Game game;
	private String name;
	private String color;
	private boolean myTurn;
	private String quitter;
	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 */
	public Client(String name, InetAddress host, int port)
			throws IOException {
		this.name = name;
		game = null;
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}
	
	public void run() {
		handleServerInput();
	}
	
	public void handleServerInput() {
		try {
	    	String msg;
	    	while ((msg = in.readLine()) != null) {
	    		try {
    				String[] msgParts = msg.split(" ");
    				Keyword keyword = Keyword.valueOf(msgParts[0]);
    				switch (keyword) {
		    			case WAITING: 
		    				print("You are in the que waiting for somebody");
		    				break;
		    			case READY: 
		    				print("You are going to play now :)");
		    				color = msgParts[1];
		    				setMyTurn(color);
		    				Stone s1 = stringToStone(color);
		    				Stone s2 = s1.other();
		    				Player p1 = new NetworkPlayer(name, s1);
		    				Player p2 = new NetworkPlayer(msgParts[2], s2);
		    				game = new Game(p1, p2, Integer.parseInt(msgParts[3]));
		    				game.start();
		    				break;
		    			case VALID: 
		    				String validColor = msgParts[1];
		    				if (!validColor.equals(color)) {
			    				print(validColor + " made a valid move!");
			    				int x = Integer.parseInt(msgParts[2]);
			    				int y = Integer.parseInt(msgParts[3]);
			    				Stone s = stringToStone(msgParts[1]);
			    				game.doMove(x, y, s);
			    				myTurn = true;
		    				} else {
		    					print("You made a valid move! Good for you!");
		    					myTurn = false;
		    				}
		    				break;
		    			case INVALID:
		    				quitter = msgParts[1];
		    				if (quitter.equals(color)) {
	    						print("You have made an invalid move! You will be kicked from the server");
	    					} else {
	    						print("The other one made an invalid move, he will be kicked!");
	    					}
		    				break;
		    			case PASSED:
		    				String passer = msgParts[1];
		    				print(passer + "has passed!");
		    				break;
		    			case TABLEFLIPPED:
		    				quitter = msgParts[1];
		    				print(quitter + " has tableflipped and left!");
		    				break;
		    			case END: //TODO: also reset the GUI.
		    				int blackScore = Integer.parseInt(msgParts[1]);
		    				int whiteScore = Integer.parseInt(msgParts[2]);
		    				if (blackScore == -1) {
		    					print(quitter + " is a coward and has ended the game!");
		    					if (quitter.equals(color)) {
		    						print("You are a sore looser");
		    					} else {
		    						print("You won!!!");
		    					}
		    				} else if (blackScore == whiteScore) {
		    					print("There is no winner, it's a draw!");
		    				} else if (blackScore > whiteScore) {
		    					print("Black has won with " + blackScore + " to " + whiteScore);
		    				} else {
		    					print("White has won with " + whiteScore + " to " + blackScore);
		    				}
		    				break;
		    			case CHAT: 
		    				print(msg);
		    				break;
		    			case WARNING: 
		    				print(msg);
		    				break;
		    			case EXIT: 
		    				print("You will now exit!");
		    				shutdown();
		    				break;
		    			default:
		    				print("default " + msg);
		    				break;
    				}
    			} catch	(IllegalArgumentException e) {
    				print(Keyword.WARNING + " The server gave your a unknown keyword, you have a problem");
    			}
	    	}
		} catch (IOException e) {
			System.out.println("Cannot read from serversocket");
		}
	}
	
	public static String handleTerminalInput(Client client) {
		String msg = "";
		try {
	    	BufferedReader terminalIn = new BufferedReader(new InputStreamReader(
					System.in));
	    	while ((msg = terminalIn.readLine()) != null) {
	    		try {
    				String[] msgParts = msg.split(" ");
    				Keyword keyword = Keyword.valueOf(msgParts[0]);
    				switch (keyword) {
		    			case MOVE: 
		    				if (client.game != null && client.myTurn) {
			    				try {
				    				int x = Integer.parseInt(msgParts[1]);
				    				int y = Integer.parseInt(msgParts[2]);
				    				
				    				if (client.game.isAllowed(x, y, client.stringToStone(client.color))) { 
				    					client.game.doMove(x, y, client.stringToStone(client.color));
				    					return msg; 
				    				} else {
				    					print(Keyword.WARNING + " apperently your chosen field is not valid, please try again!");
				    				}
			    				} catch (NumberFormatException e) {
			    					print(Keyword.WARNING + " you did not provide ints as coordinates, please try again!");
			    				}
		    				} else {
		    					print(Keyword.WARNING + " You tried to move before there was a game.");
		    				}
		    				break;
		    			default: 
		    				return msg;
    				}
    			} catch	(IllegalArgumentException e) {
    				return msg;
    			}
	    	}
		} catch (IOException e) {
			System.out.println("Cannot read from serversocket");
		}
		return msg;
	}
	
	
	private void setMyTurn(String c) {
		myTurn = c.equals("black");
	}
	
	private Stone stringToStone(String c) {
		return c.equals("black") ? Stone.BLACK : Stone.WHITE;
	}

	/** send a message to a ClientHandler. */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.out.println("Could not write to clienthandler");
			shutdown();
		}
	}

	/** close the socket connection. */
	public void shutdown() { //TODO: Fix nullpointerexception
		print("Closing socket connection...");
		started = false;
		try {
			in.close();
			System.in.close(); //Might be a bit harsh when someone exits TODO: keep the option open to connect to another server
			out.close();
			sock.close();
		} catch (IOException e) {
			System.out.println("Could not close the reader, writer or socket");
		}
	}
	
	private static void print(String message) {
		System.out.println(message);
	}
	
	public static String readString(String prompt) {
		System.out.print(prompt);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}
}
