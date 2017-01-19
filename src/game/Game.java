package game;

import java.util.*;
import com.nedap.go.gui.GoGUIIntegrator;

public class Game {
	Strategy random;
	public static void main(String[] args) {
		
        boolean white;
		Strategy random = new RandomStrategy();
		int boardsize = 5;
		GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, boardsize);
		Board b = new Board(boardsize, gogui);
        gogui.startGUI();
		for (int i = 0; i < 27; i++) {
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
				Thread.sleep(600);
			} catch (InterruptedException e)  {
				System.out.println("Yo, i've been interupted");
			}
		}
        /*b.setField(1, 1, Stone.BLACK);
		gogui.addStone(1, 1, false);
    	b.setField(1, 2, Stone.BLACK);
		gogui.addStone(1, 2, false);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	b.getChain(1, 1, Stone.BLACK, new HashSet<>());
    	b.setField(3, 2, Stone.BLACK);
		gogui.addStone(3, 2, false);

    	b.setField(3, 3, Stone.BLACK);
		gogui.addStone(3, 3, false);
    	b.getChain(3, 3, Stone.BLACK, new HashSet<>());

		try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	/*b.setField(2, 2, Stone.BLACK);
		gogui.addStone(2, 2, false);
    	b.getChain(2, 2, Stone.BLACK, new HashSet<>());*/
    	/*b.setField(1, 0, Stone.WHITE);
    	gogui.addStone(1, 0, true);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	b.setField(0, 1, Stone.WHITE);
    	gogui.addStone(0, 1, true);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	b.setField(0, 2, Stone.WHITE);
    	gogui.addStone(0, 2, true);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	b.setField(2, 1, Stone.WHITE);
    	gogui.addStone(2, 1, true);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	b.setField(2, 2, Stone.WHITE);
    	gogui.addStone(2, 2, true);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e)  {
			System.out.println("Yo, i've been interupted");
		}
    	b.setField(1, 3, Stone.WHITE);
    	gogui.addStone(1, 3, true);*/
	}
}
