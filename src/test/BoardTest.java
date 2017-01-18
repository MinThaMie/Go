package test;

import static org.junit.Assert.*;
import game.Board;
import game.Stone;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {
	
	private Board boardZero;
	private Board boardFive;
	private Board boardNine;

    @Before
    public void setUp() {
    	boardZero = new Board(0);
        boardFive = new Board(5);
        boardNine = new Board(9);      
    }
    @Test
    public void dimentionTest() {
    	assertEquals(0, boardZero.getBoardSize());
    	assertEquals(5, boardFive.getBoardSize());
    	assertEquals(9, boardNine.getBoardSize());
    }
    @Test
    public void testIsFieldIndex() {
        assertFalse(boardFive.isField(-1));
        assertTrue(boardFive.isField(0));
        assertTrue(boardNine.isField(79));
        assertFalse(boardFive.isField(29));
        assertFalse(boardZero.isField(0)); //A board with size zero has no fields
    }
    @Test
    public void testIsFieldCoor() {
    	assertFalse(boardFive.isField(-1, -1)); // invalid coordinate
        assertTrue(boardFive.isField(0, 0)); // minimum coordinate
        assertTrue(boardNine.isField(3, 6)); 
        assertTrue(boardFive.isField(4, 4)); //maximum coordinate
    }
    
    @Test
    public void testSetAndGetFieldIndex() {
        assertEquals(Stone.StoneColor.EMPTY, boardFive.getField(0).getColor());
        boardFive.setField(0, new Stone(Stone.StoneColor.BLACK));
        assertEquals(Stone.StoneColor.BLACK, boardFive.getField(0).getColor());
    }
    @Test
    public void testSetAndGetFieldCoor() {
        assertEquals(Stone.StoneColor.EMPTY, boardFive.getField(0, 0).getColor());
        boardFive.setField(0, new Stone(Stone.StoneColor.BLACK));
        assertEquals(Stone.StoneColor.BLACK, boardFive.getField(0, 0).getColor());
    }
   
}
