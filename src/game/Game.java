package game;

import com.nedap.go.gui.GoGUIIntegrator;

public class Game {
	Strategy random;
	public static void main(String[] args) {
		
        boolean white;
		Strategy random = new RandomStrategy();
		Board b = new Board(5);
		GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, b.getBoardSize());
        gogui.startGUI();
		for (int i = 0; i < 25; i++) {
			Stone stone;
			if (i % 2 == 0) {
				stone = Stone.BLACK;
				white = false;
			} else {
				stone = Stone.WHITE;
				white = true;
			}
			int move = random.determineMove(b, stone);
			b.setField(move, stone);
			int[] coor = b.coordinate(move);
			gogui.addStone(coor[0], coor[1], white);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)  {
				System.out.println("Yo, i've been interupted");
			}
		}
	}
}
