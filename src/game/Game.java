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
	private Set<List<Stone>> boards;
	private int scoreBlack;
	private int scoreWhite;
	
	public Game(int dim) {
		board = new Board(dim);
		boards = new HashSet<>();
	}
	
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
    	}
    }
	
	public Set<List<Stone>> getHistory() {
		return this.boards;
	}
	
	public void addBoard() {
		List<Stone> historyBoard = copyBoard();
    	boards.add(historyBoard);
	}
	
	public List<Stone> copyBoard() {
		Stone[] currentBoard = board.getFields();
		List<Stone> historyBoard = new ArrayList<>();
		for (int i = 0; i < currentBoard.length; i++) {
			historyBoard.add(currentBoard[i]);
		}
		return historyBoard;
	}
	
	public void calculateScore() {
		List<Stone> finalBoard = copyBoard();
		int blackStones = Collections.frequency(finalBoard, Stone.BLACK);
		int whiteStones = Collections.frequency(finalBoard, Stone.WHITE);
		
		scoreBlack = blackStones;
		scoreWhite = whiteStones;
		// for each empty field check whether it's a territory
	}
	
	public Stone determineTerritory(Set<Integer> territoryNeighbours) {
		if (territoryNeighbours.contains(Stone.BLACK) && territoryNeighbours.contains(Stone.WHITE)) {
			return null;
		} else if (territoryNeighbours.contains(Stone.BLACK)) {
			return Stone.BLACK;
		} else {
			return Stone.WHITE;
		}
	}
	
	public Set<Integer> getTerritoryNeighbours(int x, int y, Stone s, Set<Integer> territoryNeighbours, Set<Integer> chain) {
		territoryNeighbours.addAll(territoryNeighbours);
		chain.addAll(chain);
		int pos = board.index(x, y);
		Set<Integer> neighbours = board.getNeighbours(x, y);
		for (int i : neighbours) {
			if (board.getField(i) != Stone.EMPTY) {
				chain.add(pos);
				territoryNeighbours.add(i);
			} else if (!chain.contains(i) && board.getField(i) == s) {
				int[] coor = board.coordinate(i);
				chain.add(pos);
				territoryNeighbours.addAll(getTerritoryNeighbours(coor[0], coor[1], s, territoryNeighbours, chain));
			}
		}
		return territoryNeighbours;
	}

	public int getScore(Stone stone) {
		if (stone == Stone.BLACK) {
			return scoreBlack;
		} else {
			return scoreWhite;
		}
	}
    /**
     * Prints the game situation.
     */
    private void showBoardState() {
        System.out.println("\ncurrent game situation: \n\n" + Arrays.toString(board.getFields())
                + "\n");
    }
}
