package game;

import java.util.*;
import com.nedap.go.gui.GoGUIIntegrator;

public class Game {
	
	public static final int NUMBER_PLAYERS = 2;

    private Board board;
    private Player[] players;
    private int current;
	private GoGUIIntegrator gogui;
	private boolean playing;
	private Set<Stone[]> boards;
	
	public Game(Player s0, Player s1, int dim) {
		gogui = new GoGUIIntegrator(true, true, dim);
		board = new Board(dim, gogui);
		boards = new HashSet<>();
        gogui.startGUI();
        players = new Player[NUMBER_PLAYERS];
        players[0] = s0;
        players[1] = s1;
        current = 0;
    }
	
	public void start() {
		playing = true;
		play();
	}
	
	private void play() {
    	this.showBoardState();
    	while (playing) {
	    	players[current].takeTurn(board);
	    	addBoard();
	    	current = (current == 0) ? 1 : 0;
	    	this.showBoardState();
	    	System.out.println("amount of boards saved " + boards.size());
    	}
    }
	
	public Set<Stone[]> getHistory() {
		return this.boards;
	}
	
	//TODO: Think of a test
	public void addBoard() {
    	boards.add(board.getFields());
	}

    /**
     * Prints the game situation.
     */
    private void showBoardState() {
        System.out.println("\ncurrent game situation: \n\n" + Arrays.toString(board.getFields())
                + "\n");
    }
}
