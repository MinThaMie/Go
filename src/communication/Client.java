package communication;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.nedap.go.gui.GoGUIIntegrator;

import communication.ClientHandler.Keyword;
import game.*;
import player.ComputerPlayer;
import player.NetworkPlayer;
import player.Player;


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
			boolean connected = false;
			while (!connected) {
				try {
					port = Integer.parseInt(readString("To which port do you want to connect: ")); 
				} catch (NumberFormatException e) {
					print("ERROR: you did not send a number!");
				}
				try {
					Socket testSock = new Socket(host, port);
					connected = true;
					testSock.close();
				} catch (IOException e) {
					print("You did not choose the correct port");
				}
			}
		}

		
		String myName = readString("Please tell me your name: ");
		while (myName.length() > 20 || myName.contains(" ")) {
			print("You name is either too long (> 20 chars) or contains a space");
			myName = readString("Please try again: ");
		}
		String player = readString("Please tell me, do you to play yourself or let an AI do all the work?" + '\n' +
				"press 1 if you want to play yourself, and 2 as AI: ");
		if (!(player.equals("1") || player.equals("2"))) {
			print("You did not choose a valid option!");
			player = readString("Please try again: ");
		}
		
		try {
			Client client = new Client(myName, host, port, player);
			System.out.println("Im trying to connect to " + host + " and port " + port);
			client.sendMessage(Keyword.PLAYER + " " + myName);

			client.start();
			
			while (client.sock.isConnected() && !client.sock.isClosed()) {
				if (client.player.equals("2")) {
					if (client.game == null) {
						String msg = readString("1");
						client.sendMessage(msg);
					}
					while (client.game != null && client.myTurn) {
						Player p = client.game.getPlayers().get("random");
						int move = p.determineMove(client.game.getBoard());
						Stone s = client.stringToStone(client.color);
						if (move >= 0) {
							int[] coor = client.game.getBoard().coordinate(move); 
							if (client.game.isAllowed(coor[0], coor[1], s)) {
								client.game.doMove(coor[0], coor[1], s);
								client.sendMessage(Keyword.MOVE + " " + coor[0] + " " + coor[1]);
							} else {
								client.sendMessage(Keyword.PASS + "");
							}
						} else {
							client.sendMessage(Keyword.PASS + "");
						}
						client.myTurn = false;
					}

				} else {
					String input = handleTerminalInput(client);
					client.sendMessage(input);
				}
			} 
			client.shutdown();
		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
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
	private String player;
	private GoGUIIntegrator gui;
	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 */
	public Client(String name, InetAddress host, int port, String player)
			throws IOException {
		this.name = name;
		this.player = player;
		game = null;
		gui = new GoGUIIntegrator(true, true, 3);
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}
	
	public void run() {
		handleServerInput();
	}
	/**
	 * This function reads the messages send by the server.
	 * It splits the message into piece so the first piece is the Keyword and depending on the keyword different actions are taken.
	 * READY starts a game locally for the client
	 * VALID places a stone on the board if the move was from the opponent because own stone is already placed
	 * END calculates the score and tells the client who the winner is
	 */
	public void handleServerInput() {
		try {
	    	String msg;
	    	while ((msg = in.readLine()) != null) {
	    		try {
    				String[] msgParts = msg.split(" ");
    				Keyword keyword = Keyword.valueOf(msgParts[0]);
    				switch (keyword) {
		    			case WAITING: 
		    				print("You are in the queue waiting for somebody");
		    				break;
		    			case READY: 
		    				color = msgParts[1];
		    				print("You are going to play now :) and you are " + color + " and playing against " + msgParts[2]);
		    				setMyTurn(color);
		    				Stone s1 = stringToStone(color);
		    				Stone s2 = s1.other();
		    				Player p1 = null;
		    				if (player.equals("1")) {
		    					p1 = new NetworkPlayer(name, s1);
		    				} else {
		    					p1 = new ComputerPlayer(s1);
		    				}
		    				Player p2 = new NetworkPlayer(msgParts[2], s2);
		    				gui.clearBoard();
		    				gui.setBoardSize(Integer.parseInt(msgParts[3]));
		    				game = new Game(p1, p2, Integer.parseInt(msgParts[3]), gui);
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
		    				if (!passer.equals(color)) {
		    					print(passer + " has passed!");
			    				myTurn = true;
		    				} else {
		    					print("You passed! Good for you!");
		    					myTurn = false;
		    				}
		    				break;
		    			case TABLEFLIPPED:
		    				quitter = msgParts[1];
		    				print(quitter + " has tableflipped and left!");
		    				break;
		    			case END:
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
		    				gui.clearBoard();
		    				print("Type GO with a boardsize to play again");
		    				break;
		    			case CHAT: 
		    				print(msg);
		    				break;
		    			case WARNING: 
		    				print(msg);
		    				break;
		    			default:
		    				print("default " + msg);
		    				break;
    				}
    			} catch	(IllegalArgumentException e) {
    				print(Keyword.WARNING + " The server gave your a unknown keyword, you have a problem " + msg);
    			}
	    	}
		} catch (IOException e) {
			System.out.println("Cannot read from serversocket");
		}
	}
	/**
	 * Because not all terminalInput should be send directly to the server this function checks the input.
	 * Because it needs to do this in the main it's a static function and the client object is passed along to access the methods and properties.
	 * The three keywords that should be checked in advance or are not relevant for the server are: MOVE, PASS and HINT.
	 * The first two need additional checking to limit the chance of a server kick.
	 * MOVE: check if a move is allowed, otherwise ask again.
	 * PASS: check if it's your turn, otherwise ask again.
	 * HINT: just relevant for the player, so not passed to the server at all.
	 */
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
		    				if (client.game != null) {
		    					if (client.myTurn) {
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
		    						print(Keyword.WARNING + " You tried to move before your turn.");
		    					}
		    				} else {
		    					print(Keyword.WARNING + " You tried to move before there was a game.");
		    				}
		    				break;
		    			case PASS:
		    				if (client.game != null) {
		    					if (client.myTurn) {
		    						return msg;
			    				} else {
			    					print(Keyword.WARNING + " You tried to pass before your turn.");
			    				} 
		    				} else {
		    					print(Keyword.WARNING + " You tried to move before there was a game.");
		    				}
		    				break;
		    			case HINT:
		    				print(client.askForHint(client.stringToStone(client.color)));
		    				break;
		    			case EXIT: 
		    				print("You will now exit!");
		    				return msg;
		    			default: 
		    				return msg;
    				}
    			} catch	(IllegalArgumentException e) {
    				return msg;
    			}
	    	}
		} catch (IOException e) {
			System.out.println("Cannot read from clientsocket");
		}
		return msg;
	}
	
	/**
	 * Checks the board for the first available and allowed intersection an returns this as help message.
	 * @param s: Stone passed to do the isAllowed check.
	 */
	private String askForHint(Stone s) {
		Board board = this.game.getBoard();
		for (int x = 0; x < board.getBoardSize(); x++) {
			for (int y = 0; y < board.getBoardSize(); y++) {
				if (game.isAllowed(x, y, s)) {
					return "You can put your stone at " + x + " " + y; 
				}
			}	
		}
		return "There is no move you can do!";
		
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
			System.out.println("Apperently the server has disconnected, you will also be shut down.");
			shutdown();
		}
	}

	/** close the socket connection. */
	public void shutdown() {
		print("You have shutted down!");
		try {
			in.close();
			System.in.close();
			out.close();
			sock.close();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Could not close the reader, writer or socket");
		}
	}
	
	private static void print(String message) {
		System.out.println(message);
	}
	
	public static String readString(String prompt) {
		System.out.print(prompt);
		String msg = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			msg = in.readLine();
		} catch (IOException e) {
		}

		return (msg == null) ? "" : msg;
	}
}
