package communication;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.ClientHandler.Keyword;


public class Client extends Thread {
	public static void main(String[] args) {
		InetAddress host = null;
		int port = 0;
		
		while (host == null) {
			try {
				host = InetAddress.getByName(readString("To which ip-adress do you want to connect: ")); //takes a string
			} catch (UnknownHostException e) {
				print("ERROR: no valid hostname! Please try again.");
			}
		}
		while (port == 0) {
			try {
				port = Integer.parseInt(readString("To which port do you want to connect: ")); 
			} catch (NumberFormatException e) {
				print("ERROR: no valid portnummer!");
			}
		}

		try {
			String myName = readString("Please tell me your name: ");
			Client client = new Client(myName, host, port);
			System.out.println("Im trying to connect to " + host + " and port " + port);
			client.sendMessage(myName);
			client.start();
			
			do {
				String input = readString("");
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

	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 */
	public Client(String name, InetAddress host, int port)
			throws IOException {
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}
	
	public void run() {
		handleServerInput();
	}
	
	public void handleServerInput() {
		print("Welcome to our chatroom. Type exit to leave");
		try {
	    	String msg;
	    	while ((msg = in.readLine()) != null) {
	    		print(msg);
				String[] msgParts = msg.split(" ");
				Keyword keyword = Keyword.valueOf(msgParts[0]);
				switch (keyword) {
	    			case WAITING: 
	    				print("You are in the que waiting for somebody");
	    				break;
	    			case READY: 
	    				print("You are going to play now :)");
	    				Stone s1 = stringToStone(msgParts[1]);
	    				Stone s2 = s1.other();
	    				Player p1 = new HumanPlayer(name, s1);
	    				Player p2 = new HumanPlayer(msgParts[2], s2);
	    				game = new Game(p1, p2, Integer.parseInt(msgParts[3]));
	    				game.start();
	    				break;
	    			default: 
	    				print("The server provided you with an unknown keyword, you have a problem!");
        				break;
				}
	    	}
		} catch (IOException e) {
			System.out.println("Cannot read from serversocket");
		}
	}
	
	private Stone stringToStone(String color) {
		return color.equals("black") ? Stone.BLACK : Stone.WHITE;
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
