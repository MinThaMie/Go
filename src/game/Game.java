package game;

public class Game {

	public static void main(String[] args) {
		Board b = new Board(5);
		b.setField(3, Stone.BLACK);
		b.setField(1, 1, Stone.WHITE);
	}
}
