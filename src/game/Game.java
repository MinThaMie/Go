package game;

import com.nedap.go.gui.GoGUIIntegrator;

public class Game {
	Strategy random;
	public static void main(String[] args) {
		Strategy random = new RandomStrategy();
		int boardsize = 5;
		GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, boardsize);
		Board b = new Board(boardsize, gogui);
        gogui.startGUI();
		for (int i = 0; i < 27; i++) {
			Stone stone;
			if (i % 2 == 0) {
				stone = Stone.BLACK;
			} else {
				stone = Stone.WHITE;
			}
			int move = random.determineMove(b, stone);
			b.setField(move, stone); 
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)  {
				System.out.println("Yo, i've been interupted");
			}
		}
	}
}
