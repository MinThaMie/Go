package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.Stone;
import game.Game;

public class GameTest {
	
	private Game game;
	private Board board;
	@Before
    public void setUp() {
		game = new Game(5);
	}
	@Test
	public void testScore() {
		board.testField(0, 0, Stone.BLACK);
    	board.testField(2, 0, Stone.WHITE);
    	board.testField(0, 1, Stone.WHITE);
    	board.testField(1, 1, Stone.WHITE);
    	board.testField(1, 0, Stone.BLACK);
    	game.calculateScore();
    	assertEquals(2, game.getScore(Stone.BLACK));
    	assertEquals(3, game.getScore(Stone.WHITE));
    	
	}

}
