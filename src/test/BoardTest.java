package test;

import static org.junit.Assert.*;

import java.util.HashSet;

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
        assertEquals(Stone.EMPTY, boardFive.getField(0));
        boardFive.setField(0, Stone.BLACK);
        assertEquals(Stone.BLACK, boardFive.getField(0));
    }
    @Test
    public void testSetAndGetFieldCoor() {
        assertEquals(Stone.EMPTY, boardFive.getField(0, 0));
        boardFive.setField(0, 0, Stone.BLACK);
        assertEquals(Stone.BLACK, boardFive.getField(0, 0));
    }
    
    @Test
    public void testNeighbours() {
    	boardFive.setField(0, 0, Stone.BLACK);
    	assertEquals(2, boardFive.getNeighbours(0, 0).size());
    	boardFive.setField(3, 2, Stone.BLACK);
    	assertEquals(4, boardFive.getNeighbours(1, 1).size());
    }
   
    @Test
    public void testLibertiesCorner() {
    	assertTrue(boardFive.hasLiberties(0, 0, Stone.WHITE));
    	boardFive.setField(0, 1, Stone.BLACK);
    	assertTrue(boardFive.hasLiberties(0, 0, Stone.WHITE));
    	boardFive.setField(1, 0, Stone.BLACK);
    	assertFalse(boardFive.hasLiberties(0, 0, Stone.WHITE));
    }
    
    @Test
    public void testLibertiesEdge() {
    	assertTrue(boardFive.hasLiberties(1, 0, Stone.WHITE));
    	boardFive.setField(0, 0, Stone.BLACK);
    	assertTrue(boardFive.hasLiberties(1, 0, Stone.WHITE));
    	boardFive.setField(2, 0, Stone.BLACK);
    	boardFive.setField(1, 1, Stone.BLACK);
    	assertFalse(boardFive.hasLiberties(1, 0, Stone.WHITE));
    }
   
    @Test
    public void testLiberties() {
    	boardNine.setField(0, 2, Stone.BLACK);
    	boardNine.setField(1, 1, Stone.BLACK);
    	boardNine.setField(2, 2, Stone.BLACK);
    	boardNine.setField(1, 2, Stone.WHITE);
    	assertTrue(boardNine.hasLiberties(1, 2, Stone.WHITE));
    	boardNine.setField(1, 3, Stone.WHITE);
    	assertTrue(boardNine.hasLiberties(1, 2, Stone.WHITE)); 
    }

    @Test
    public void testGetLiberties() {
    	boardNine.setField(0, 2, Stone.BLACK);
    	boardNine.setField(1, 1, Stone.BLACK);
    	assertEquals(4, boardNine.getLiberties(1, 1, Stone.BLACK, new HashSet<>(), new HashSet<>()).size());
    	boardNine.setField(2, 2, Stone.BLACK);    	
    	boardNine.setField(1, 2, Stone.WHITE);
    	assertEquals(1, boardNine.getLiberties(1, 2, Stone.WHITE, new HashSet<>(), new HashSet<>()).size());
    	boardNine.setField(1, 3, Stone.WHITE);
    	assertEquals(3, boardNine.getLiberties(1, 2, Stone.WHITE, new HashSet<>(), new HashSet<>()).size());
    	assertEquals(3, boardNine.getLiberties(1, 3, Stone.WHITE, new HashSet<>(), new HashSet<>()).size());
    	boardNine.setField(1, 4, Stone.WHITE);
    	boardNine.setField(2, 4, Stone.WHITE);
    	assertEquals(6, boardNine.getLiberties(1, 2, Stone.WHITE, new HashSet<>(), new HashSet<>()).size());
    }
    
    @Test
    public void testSquare() {
    	boardNine.setField(1, 2, Stone.BLACK);
    	boardNine.setField(2, 2, Stone.BLACK);
    	boardNine.setField(1, 3, Stone.BLACK);
    	boardNine.setField(2, 3, Stone.BLACK);
    	assertEquals(8, boardNine.getLiberties(1, 2, Stone.BLACK, new HashSet<>(), new HashSet<>()).size());
    }
    
    @Test
    public void testChains() {
    	boardNine.setField(1, 1, Stone.BLACK);
    	boardNine.setField(1, 2, Stone.BLACK);
    	boardNine.setField(3, 2, Stone.BLACK);
    	boardNine.setField(3, 3, Stone.BLACK);
    	assertEquals(6, boardNine.getLiberties(1, 2, Stone.BLACK, new HashSet<>(), new HashSet<>()).size());
    	boardNine.setField(2, 2, Stone.BLACK);
    	assertEquals(10, boardNine.getLiberties(1, 2, Stone.BLACK, new HashSet<>(), new HashSet<>()).size());
    }
    @Test
    public void testRemove() {
    	boardFive.setField(0, 0, Stone.BLACK);
    	boardFive.setField(0, 1, Stone.WHITE);
    	assertEquals(Stone.BLACK, boardFive.getField(0, 0));
    	boardFive.setField(1, 0, Stone.WHITE);
    	assertEquals(Stone.EMPTY, boardFive.getField(0, 0));

    }
    @Test
    
    public void testPlacement() {
    	boardFive.setField(0, 1, Stone.WHITE);
    	boardFive.setField(1, 0, Stone.WHITE);
    	assertFalse(boardFive.isAllowed(0, 0, Stone.BLACK));
    	assertTrue(boardFive.isAllowed(0, 0, Stone.WHITE));
    	boardFive.setField(0, 0, Stone.BLACK);
    	assertEquals(Stone.EMPTY, boardFive.getField(0, 0));
    	assertFalse(boardFive.isAllowed(0, 1, Stone.BLACK));
    }
}
