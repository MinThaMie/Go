package game;

import java.util.*;
import com.nedap.go.gui.GoGUIIntegrator;

public class Game extends Thread {
	
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
	
	public void run() {
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
		List<Stone> historyBoard = listBoard();
    	boards.add(historyBoard);
	}
	
	public List<Stone> listBoard() {
		Stone[] currentBoard = board.getFields();
		List<Stone> historyBoard = new ArrayList<>();
		for (int i = 0; i < currentBoard.length; i++) {
			historyBoard.add(currentBoard[i]);
		}
		return historyBoard;
	}
	
	public void calculateScore() {
		List<Stone> finalBoard = listBoard();
		int blackStones = Collections.frequency(finalBoard, Stone.BLACK);
		int whiteStones = Collections.frequency(finalBoard, Stone.WHITE);
		
		scoreBlack = blackStones;
		scoreWhite = whiteStones;
		Map<Stone, Integer> territoryScore = getTerritoryScore(finalBoard);
		scoreBlack = scoreBlack + territoryScore.get(Stone.BLACK);
		scoreWhite = scoreWhite + territoryScore.get(Stone.WHITE);

		// for each empty field check whether it's a territory
	}
	
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
	
	public String getWinner() {
		String winner;
		if (scoreBlack == scoreWhite) {
			winner = "There is no winner, it's a draw!";
		} else if (scoreBlack > scoreWhite) {
			winner = "Black has won!";
		} else {
			winner = "White has won";
		}
		return winner;
	}
	
	//TODO: check this with player.TakeTurn
	public void makeMove(int x, int y, Stone s) {
		if (isAllowed(x, y, s)) {
			board.testField(x, y, s);
		} else {
			System.out.println("You have been removed from the server");
		}
	}
	
    /**
     * Prints the game situation.
     */
    private void showBoardState() {
        System.out.println("\ncurrent game situation: \n\n" + Arrays.toString(board.getFields())
                + "\n");
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
