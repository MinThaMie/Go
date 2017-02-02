package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Go {
	private final static int DEFAULT_SIZE = 9;
	public Go() {
		
	}
	public static void main(String[] args) {
		Go go = new Go();
    	Player p1 = new HumanPlayer(go.askName(), Stone.BLACK);
    	Player p2 = new HumanPlayer(go.askName(), Stone.WHITE);
    	Game game = new Game(p1, p2, go.askBoardSize(), true);
    	game.start();
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
	
	private int askBoardSize() {
		BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("What do you want the boardsize to be?");
        int boardsize = DEFAULT_SIZE;
        try {
        	boardsize = Integer.parseInt(line.readLine());
        } catch (IOException e) {
        	System.out.println("There is no reader");
        }
		return boardsize;
	}
}
