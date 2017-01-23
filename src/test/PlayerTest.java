package test;

import static org.junit.Assert.*;
import game.*;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
	
	private Player player;
	private Board board;

	@Before
    public void setUp() {
		player = new HumanPlayer("henk", Stone.BLACK);
		board = new Board(5);
	}
	
	@Test
	public void testCopyBoard() {
		assertArrayEquals(board.getFields(), player.copyBoard(board).getFields());
		board.testField(0, 0, Stone.BLACK);
		assertArrayEquals(board.getFields(), player.copyBoard(board).getFields());
	}
	
	@Test
	public void testAddToHistory() {
		assertEquals(0, player.getHistory().size());
		board.testField(0, 0, Stone.BLACK);
		player.addToHistory(board);
		assertEquals(1, player.getHistory().size());
	}
	
	@Test
	public void testAddSameToHistory() {
		board.testField(0, 0, Stone.BLACK);
		player.addToHistory(board);
		assertEquals(1, player.getHistory().size());
		board.testField(0, 0, Stone.BLACK);
		player.addToHistory(board);
		assertEquals(1, player.getHistory().size());
	}
	
	@Test
	public void testDifferentToHistory() {
		board.testField(0, 0, Stone.BLACK);
		player.addToHistory(board);
		assertEquals(1, player.getHistory().size());
		board.testField(0, 1, Stone.BLACK);
		player.addToHistory(board);
		assertEquals(2, player.getHistory().size());
	}

	@Test
	public void testAddCopyToHistory() {
		board.testField(0, 0, Stone.BLACK);
		player.addToHistory(board);
		assertEquals(1, player.getHistory().size());
		assertTrue(player.getHistory().contains(player.listBoard(board)));
		Board copy = player.copyBoard(board);
		player.addToHistory(copy);
		assertEquals(1, player.getHistory().size());
	}
	
	@Test
	public void testKo() {
		board.testField(2, 3, Stone.BLACK);
		player.addToHistory(board);
		Board copy = new Board(5);
		copy.testField(2, 3, Stone.BLACK);
		assertTrue(player.isKo(copy));
	}
	
	@Test
	public void testIsAllowed() {
		board.testField(2, 0, Stone.BLACK);
		player.addToHistory(board);
		board.testField(3, 1, Stone.BLACK);
		player.addToHistory(board);
		board.testField(2, 2, Stone.BLACK);
		player.addToHistory(board);
		board.testField(1, 1, Stone.BLACK);
		player.addToHistory(board);
		board.testField(1, 0, Stone.WHITE);
		player.addToHistory(board);
		board.testField(0, 1, Stone.WHITE);
		player.addToHistory(board);
		board.testField(1, 2, Stone.WHITE);
		player.addToHistory(board);
		assertEquals(7, player.getHistory().size());
		board.testField(2, 1, Stone.WHITE); //move takes black 1 1
		assertFalse(player.isAllowed(board, 1, 1, Stone.BLACK));

	}
}
