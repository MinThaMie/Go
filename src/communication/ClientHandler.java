package communication;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Server server;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientName;
    
    public enum Keyword { PLAYER, EXIT, GO, WAITING, READY, MOVE, VALID, INVALID, PASS, PASSED, TABLEFLIP, TABLEFLIPPED, CHAT, WARNING, END, CANCEL }
    
    public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
    	server = serverArg;
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
        	
        	String msg;
    		while ((msg = in.readLine()) != null) {
    			server.print(msg); //To meet requirement 4 for the server, page 7
    			try {
    				String[] msgParts = msg.split(" ");
    				Keyword keyword = Keyword.valueOf(msgParts[0]);
    				switch (keyword) {
		    			case EXIT :
		    				server.broadcast(Keyword.WARNING + " " + clientName + " has left the server");
		    				shutdown();
		    				break;
		    			case GO:
		    				server.addToGameLobby(clientName, Integer.parseInt(msgParts[1]), this); //TODO: implement check for boardconstraints
		    				break;
		    			case CANCEL: 
		    				server.removeFromGameLobby(clientName);
		    				break;
		    			case CHAT: 
		    				server.broadcast(msg);
		    				break;
    				}
    			} catch	(IllegalArgumentException e) {
    				sendMessage(Keyword.WARNING + " The server does not now this keyword");
    			}
    			
    		}
    	} catch (IOException e) {
    		shutdown();
    	} 
		shutdown();
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
        } catch (IOException e) {
    		e.printStackTrace();
    	} 
    }
    
    public String getClientName() {
    	return clientName;
    }
}
