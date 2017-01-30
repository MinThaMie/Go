package communication;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Server server;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientName;

    public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
    	server = serverArg;
    	in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
    	out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));
    }

    public void announce() throws IOException {
        clientName = in.readLine();
        server.broadcast("[" + clientName + " has entered] ");
    }

    public void run() {
    	try {
        	announce();
        	
        	String msg;
    		while ((msg = in.readLine()) != null) {
    			if (msg.equals("exit")) {
    				System.out.println("This client wants to exit " + clientName);
	    			break;
	    		} else {
	    			server.broadcast("[" + clientName + "] " + msg);
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
}
