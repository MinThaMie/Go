package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Stone;
import game.Game;

public class GameTest {
	
	private Game game;

	@Before
    public void setUp() {
		game = new Game(5);
	}
	@Test
	public void testSimpleScore() {
		game.makeMove(0, 0, Stone.BLACK);
    	game.makeMove(2, 0, Stone.WHITE);
    	game.makeMove(0, 1, Stone.WHITE);
    	game.makeMove(1, 1, Stone.WHITE);
    	game.makeMove(3, 0, Stone.BLACK);
    	game.calculateScore();
    	assertEquals(2, game.getScore(Stone.BLACK));
    	assertEquals(3, game.getScore(Stone.WHITE));	
	}
	
	@Test
	public void testTerritoryScore() {
    	game.makeMove(2, 0, Stone.WHITE);
    	game.makeMove(0, 1, Stone.WHITE);
    	game.makeMove(1, 1, Stone.WHITE);
    	game.makeMove(3, 0, Stone.BLACK);
    	game.makeMove(4, 1, Stone.BLACK);
    	game.calculateScore();
    	assertEquals(3, game.getScore(Stone.BLACK));
    	assertEquals(5, game.getScore(Stone.WHITE));	
	}
	
	@Test
	public void testComplexTerritoryScore() {
    	game.makeMove(2, 0, Stone.WHITE);
    	game.makeMove(0, 1, Stone.WHITE);
    	game.makeMove(1, 1, Stone.WHITE);
    	game.makeMove(1, 1, Stone.WHITE);	
    	game.makeMove(0, 2, Stone.WHITE);
    	game.makeMove(1, 3, Stone.WHITE);
    	game.makeMove(2, 3, Stone.WHITE);
    	game.makeMove(3, 2, Stone.WHITE);
    	game.makeMove(2, 1, Stone.WHITE);
    	
    	game.makeMove(3, 0, Stone.BLACK);
    	game.makeMove(4, 1, Stone.BLACK);
    	game.makeMove(4, 3, Stone.BLACK);
    	game.makeMove(3, 3, Stone.BLACK);
    	game.makeMove(2, 4, Stone.BLACK);
    	game.makeMove(0, 4, Stone.BLACK);
  
    	game.calculateScore();
    	assertEquals(9, game.getScore(Stone.BLACK));
    	assertEquals(12, game.getScore(Stone.WHITE));	
	}

}
