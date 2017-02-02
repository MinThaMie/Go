package game;

import java.util.*;
import com.nedap.go.gui.GoGUIIntegrator;

import player.Player;

public class Game extends Thread {
	
	public static final int NUMBER_PLAYERS = 2;

    private Board board;
    private HashMap<String, Player> players;
    private int current;
	private GoGUIIntegrator gogui;
	private Set<List<Stone>> boards;
	private int scoreBlack;
	private int scoreWhite;
    private int passes;
    private String firstPasser;

	/**
	 * Constructor of basic board, used for testing.
	 * @param dimension of the board
	 */
	public Game(int dim) {
		board = new Board(dim);
		boards = new HashSet<>();
	}
	
	/**
	 * Constructor for the server a.o. without a GUI.
	 * Including players and ways to keep track of the passes and turns.
	 */
	public Game(Player s0, Player s1, int dim) {
		this(dim);
        players = new HashMap<>();
        players.put(s0.getName(), s0);
        players.put(s1.getName(), s1);
        current = 0;
        passes = 0;
        firstPasser = null;
	}
	
	/**
	 * Including the GUI.
	 */
	public Game(Player s0, Player s1, int dim, GoGUIIntegrator gui) {
		this(s0, s1, dim);
		board = new Board(dim, gui);
		this.gogui = gui;
		gogui.startGUI();
		
    }
	
	public HashMap<String, Player> getPlayers() {
		return this.players;
	}
	
	public String getCurrentPlayer() {
		return current == 0 ? "black" : "white";
	}
	
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Allows clients and the server to do moves on the game.
	 * Sleeps 1 second to prevent AIs from making moves to fast.
	 */
	public void doMove(int x, int y, Stone s) {
		if (gogui == null) {
			board.testField(x, y, s);
		} else {
			board.setField(x, y, s);
		}
		addBoard();
		try {
    		sleep(1000);
    	} catch (InterruptedException e) {
    		System.err.println("I've been summonned but I wanted to sleep");
    	}
		current = (current == 0) ? 1 : 0;
	}
	
	public int getPasses() {
		return passes;
	}
	
	public void setFirstPasser(String color) {
		firstPasser = color;
	}
	
	public String getFirstPasser() {
		return firstPasser;
	}
	
	public void resetPasses() {
		passes = 0;
	}
	
	public void setPasses(int newAmount) {
		passes = newAmount;
		current = (current == 0) ? 1 : 0;
	}
	
	public Set<List<Stone>> getHistory() {
		return this.boards;
	}
	
	/**
	 * Add a board to the history, relevant for Ko.
	 */
	public void addBoard() {
		List<Stone> historyBoard = listBoard();
    	boards.add(historyBoard);
	}
	
	/**
	 * Creates a deep copy of the board, but as a list to be stored but not used for manipulation.
	 */
	public List<Stone> listBoard() {
		Stone[] currentBoard = board.getFields();
		List<Stone> historyBoard = new ArrayList<>();
		for (int i = 0; i < currentBoard.length; i++) {
			historyBoard.add(currentBoard[i]);
		}
		return historyBoard;
	}
	
	/**
	 * Calculates first the amount of white and black stones on the board and then the territories.
	 */
	public void calculateScore() {
		List<Stone> finalBoard = listBoard();
		int blackStones = Collections.frequency(finalBoard, Stone.BLACK);
		int whiteStones = Collections.frequency(finalBoard, Stone.WHITE);
		
		scoreBlack = blackStones;
		scoreWhite = whiteStones;
		Map<Stone, Integer> territoryScore = getTerritoryScore(finalBoard);
		scoreBlack = scoreBlack + territoryScore.get(Stone.BLACK);
		scoreWhite = scoreWhite + territoryScore.get(Stone.WHITE);
	}
	
	/**
	 * This function gets all the empty intersections and checks if those are only surrounded by a certain color.
	 */
	private Map<Stone, Integer> getTerritoryScore(List<Stone> finalBoard) {
		Set<Integer> checkedStones = new HashSet<>();
		Map<Stone, Integer> territoryScore = new HashMap<>();
		territoryScore.put(Stone.BLACK, 0);
		territoryScore.put(Stone.WHITE, 0);
		for (int i = 0; i < finalBoard.size(); i++) {
			if (finalBoard.get(i) == Stone.EMPTY && !checkedStones.contains(i)) {
				int[] coor = board.coordinate(i);
				Set<Integer> emptyTerritory = board.getChain(coor[0], coor[1], finalBoard.get(i), new HashSet<>());
				checkedStones.addAll(emptyTerritory);
				Set<Integer> enclosingStones = board.getNeighbours(emptyTerritory);
				Integer[] stones = enclosingStones.toArray(new Integer[enclosingStones.size()]);
				Stone firstColor = board.getField(stones[0]);
				int colorCount = 0;
				for (int j : stones) {
					if (firstColor == board.getField(j)) {
						colorCount++;
					} else {
						break;
					}
				}
				if (colorCount == stones.length) {
					territoryScore.put(firstColor, territoryScore.get(firstColor) + emptyTerritory.size());
				}
			}
		}
		return territoryScore;
	}

	public int getScore(Stone stone) {
		if (stone == Stone.BLACK) {
			return scoreBlack;
		} else {
			return scoreWhite;
		}
	}
	public void makeMove(int x, int y, Stone s) {
		if (isAllowed(x, y, s)) {
			board.testField(x, y, s);
		} else {
			System.out.println("You have been removed from the server");
		}
	}
    
    //----Rules----
    
    public boolean isAllowed(int x, int y, Stone s) {
    	boolean isEmpty = board.isEmpty(x, y);
		Board copy = copyBoard();
		copy.testField(x, y, s);
		boolean ko = isKo(copy);
		return isEmpty && !ko;
	}
	
	public boolean isKo(Board copy) {
		return boards.contains(listBoard(copy));
	}
	public List<Stone> listBoard(Board copy) {
		Stone[] currentBoard = copy.getFields();
		List<Stone> historyBoard = new ArrayList<>();
		for (int i = 0; i < currentBoard.length; i++) {
			historyBoard.add(currentBoard[i]);
		}
		return historyBoard;
	}
	
	public Board copyBoard() {
		Stone[] currentBoard = board.getFields();
		Board copyBoard = new Board(board.getBoardSize());
		for (int i = 0; i < currentBoard.length; i++) {
			if (!(currentBoard[i] == Stone.EMPTY)) {
				copyBoard.testField(i, currentBoard[i]);
			}
		}
		return copyBoard;
	}
}
