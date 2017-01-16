package game;

public class Game {
	Strategy random;
	public static void main(String[] args) {
		Strategy random = new RandomStrategy();
		Board b = new Board(5);
		b.setField(3, Stone.BLACK);
		b.setField(1, 1, Stone.WHITE);
		
		for (int i = 0; i < 25; i++) {
			Stone stone;
			if (i % 2 == 0) {
				stone = Stone.BLACK;
			} else {
				stone = Stone.WHITE;
			}
			b.setField(random.determineMove(b, stone), stone);	
		}
	}
}
