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
			client.sendMessage(myName);
			client.start();
			
			
			//Dit zorgt ervoor dat het direct bij enter naar de client handler wordt gestuurt
			do {
				String input = handleTerminalInput();
				client.sendMessage(input);
			} while (true);
			
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
	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 */
	public Client(String name, InetAddress host, int port)
			throws IOException {
		this.name = name;
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
	    		//print(msg);	
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
		    				print(msgParts[1] + " made a valid move!");
		    				//TODO: Flip my turn
		    				break;
		    			case CHAT: 
		    				print(msg);
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
	
	public static String handleTerminalInput() {
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
		    				print(msg);
		    				break;
    				}
    			} catch	(IllegalArgumentException e) {
    				print(Keyword.WARNING + " you typed something unknow to the server");
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
	public void shutdown() {
		print("Closing socket connection...");
		try {
			in.close();
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
