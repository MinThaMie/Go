package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Go {

	public Go() {
		
	}
	public static void main(String[] args) {
		Go go = new Go();
    	Player p1 = new HumanPlayer(go.askName(), Stone.BLACK);
    	Player p2 = new HumanPlayer(go.askName(), Stone.WHITE);
    	//Game game = new Game(p1, p2);
    	//game.start();
	}
	private String askName() {
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Please tell me your name!");
        String name = "Default";
        try {
        	name = line.readLine();
        } catch (IOException e) {
        	System.out.println("There is no reader");
        }
		return name;
	}
}
