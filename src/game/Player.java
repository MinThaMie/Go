package game;
import java.util.*;
public abstract class Player {

	String name;
	Stone color;
	Set<List<Stone>> boardHistory;
	public Player(String name, Stone stone) {
		this.name = name;
		this.color = stone;
		boardHistory = new HashSet<>();
	}
	
	public String getName() {
		return name;
	}
	
	public Stone getColor() {
		return color;
	}
	
    public abstract int determineMove(Board board);
    
    public abstract void takeTurn(Board board);
	
	public void makeMove(Board board) {
		boardHistory.add(listBoard(board));
        int choice = determineMove(board);
        board.setField(choice, getColor());
        boardHistory.add(listBoard(board));
    }
	
	public boolean isAllowed(Board board, int x, int y, Stone s) {
		boolean empty = board.isEmpty(x, y);
		Board copy = copyBoard(board);
		copy.testField(x, y, s);
		boolean ko = isKo(copy);
		return empty && !ko;
		// getHistory --> !contains --> copy of board + set  allowed else Ko
		// if x y !getLiberties && 1 of neighbours getLiberties.isEmpty --> allowed and remove neighbour
	}
	
	public Set<List<Stone>> getHistory() {
		return this.boardHistory;
	}
	
	public boolean isKo(Board board) {
		return boardHistory.contains(listBoard(board));
	}
	
	public void addToHistory(Board board) {
		boardHistory.add(listBoard(board));
	}
	public Board copyBoard(Board board) {
		Stone[] currentBoard = board.getFields();
		Board copyBoard = new Board(board.getBoardSize());
		for (int i = 0; i < currentBoard.length; i++) {
			if (!(currentBoard[i] == Stone.EMPTY)) {
				copyBoard.testField(i, currentBoard[i]);
			}
		}
		return copyBoard;
	}
	
	public List<Stone> listBoard(Board board) {
		Stone[] currentBoard = board.getFields();
		List<Stone> historyBoard = new ArrayList<>();
		for (int i = 0; i < currentBoard.length; i++) {
			historyBoard.add(currentBoard[i]);
		}
		return historyBoard;
	}
}
