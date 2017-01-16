package game;

public interface Strategy {
	public String getName();
	public int determineMove(Board b, Stone s);
}