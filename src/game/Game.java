package game;

import com.nedap.go.gui.GoGUIIntegrator;

import game.Stone.StoneColor;

public class Game {
	Strategy random;
	public static void main(String[] args) {
		
        boolean white;
		Strategy random = new RandomStrategy();
		Board b = new Board(5);
		Stone sW = new Stone(StoneColor.WHITE);
		Stone sB = new Stone(StoneColor.BLACK);
		GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, b.getBoardSize());
        gogui.startGUI();
		for (int i = 0; i < 16; i++) {
			Stone stone;
			if (i % 2 == 0) {
				stone = sB;
				white = false;
			} else {
				stone = sW;
				white = true;
			}
			int move = random.determineMove(b, stone);
			b.setField(move, stone, gogui);
			int[] coor = b.coordinate(move);
			gogui.addStone(coor[0], coor[1], white);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e)  {
				System.out.println("Yo, i've been interupted");
			}
		}
       /* b.setField(0, 0, sW, gogui);
		gogui.addStone(0, 0, true);
		System.out.println("corner " + sW.getColor());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
		b.setField(0, 1, sB, gogui);
		gogui.addStone(0, 1, false);
		System.out.println("corner " + sW.getColor());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
        b.setField(3, 2, sW, gogui);
		gogui.addStone(3, 2, true);
		System.out.println("corner " + sW.getColor());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
		b.setField(1, 0, sB, gogui);
		gogui.addStone(1, 0, false);
		System.out.println("corner " + sW.getColor());
*/
	}
}
