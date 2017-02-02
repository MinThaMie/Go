package player;

import game.Board;
import game.Stone;

public interface Strategy {
	public String getName();
	public int determineMove(Board b, Stone s);
}