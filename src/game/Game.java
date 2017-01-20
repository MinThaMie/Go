package game;

import java.util.*;
import com.nedap.go.gui.GoGUIIntegrator;

public class Game {
	
	public static final int NUMBER_PLAYERS = 2;

    private Board board;
    private Player[] players;
    private int current;
	
	
	public Game(Player s0, Player s1, int dim) {
		GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, dim);
		board = new Board(dim, gogui);
        gogui.startGUI();
        players = new Player[NUMBER_PLAYERS];
        players[0] = s0;
        players[1] = s1;
        current = 0;
    }
	
	public void start() {
		play();
	}
	
	private void play() {
    	this.update();
    	int i = 0;
    	while (i < 25) {
	    	players[current].takeTurn(board);
	    	current = (current == 0) ? 1 : 0;
	    	this.update();
	    	i++;
    	}
    }

    /**
     * Prints the game situation.
     */
    private void update() {
        System.out.println("\ncurrent game situation: \n\n" + Arrays.toString(board.getFields())
                + "\n");
    }
}
