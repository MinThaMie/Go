package game;

public abstract class Player {

	String name;
	Stone color;
	
	public Player(String name, Stone stone) {
		this.name = name;
		this.color = stone;
	}
	
	public String getName() {
		return name;
	}
	
	public Stone getColor() {
		return color;
	}
	
    public abstract int determineMove(Board board);
	
	public void makeMove(Board board) {
        int choice = determineMove(board);
        board.setField(choice, getColor());
    }
}
