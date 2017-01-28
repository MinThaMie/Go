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
        boardFive.testField(0, Stone.BLACK);
        assertEquals(Stone.BLACK, boardFive.getField(0));
    }
    @Test
    public void testSetAndGetFieldCoor() {
        assertEquals(Stone.EMPTY, boardFive.getField(0, 0));
        boardFive.testField(0, 0, Stone.BLACK);
        assertEquals(Stone.BLACK, boardFive.getField(0, 0));
    }
    
    @Test
    public void testLibertiesCorner() {
    	assertTrue(boardFive.hasLiberties(0, 0, Stone.WHITE));
    	boardFive.testField(0, 1, Stone.BLACK);
    	assertTrue(boardFive.hasLiberties(0, 0, Stone.WHITE));
    	boardFive.testField(1, 0, Stone.BLACK);
    	assertFalse(boardFive.hasLiberties(0, 0, Stone.WHITE));
    }
    
    @Test
    public void testLibertiesEdge() {
    	assertTrue(boardFive.hasLiberties(1, 0, Stone.WHITE));
    	boardFive.testField(0, 0, Stone.BLACK);
    	assertTrue(boardFive.hasLiberties(1, 0, Stone.WHITE));
    	boardFive.testField(2, 0, Stone.BLACK);
    	boardFive.testField(1, 1, Stone.BLACK);
    	assertFalse(boardFive.hasLiberties(1, 0, Stone.WHITE));
    }
   
    @Test
    public void testLiberties() {
    	boardNine.testField(0, 2, Stone.BLACK);
    	boardNine.testField(1, 1, Stone.BLACK);
    	boardNine.testField(2, 2, Stone.BLACK);
    	boardNine.testField(1, 2, Stone.WHITE);
    	assertTrue(boardNine.hasLiberties(1, 2, Stone.WHITE));
    	boardNine.testField(1, 3, Stone.WHITE);
    	assertTrue(boardNine.hasLiberties(1, 2, Stone.WHITE)); 
    }

    @Test
    public void testGetLiberties() {
    	boardNine.testField(0, 2, Stone.BLACK);
    	boardNine.testField(1, 1, Stone.BLACK);
    	assertEquals(4, boardNine.getLiberties(1, 1, Stone.BLACK, new HashSet<>()).size());
    	boardNine.testField(2, 2, Stone.BLACK);    	
    	boardNine.testField(1, 2, Stone.WHITE);
    	assertEquals(1, boardNine.getLiberties(1, 2, Stone.WHITE, new HashSet<>()).size());
    	boardNine.testField(1, 3, Stone.WHITE);
    	assertEquals(3, boardNine.getLiberties(1, 2, Stone.WHITE, new HashSet<>()).size());
    	assertEquals(3, boardNine.getLiberties(1, 3, Stone.WHITE, new HashSet<>()).size());
    	boardNine.testField(1, 4, Stone.WHITE);
    	boardNine.testField(2, 4, Stone.WHITE);
    	assertEquals(6, boardNine.getLiberties(1, 2, Stone.WHITE, new HashSet<>()).size());
    }
    
    @Test
    public void testSquare() {
    	boardNine.testField(1, 2, Stone.BLACK);
    	boardNine.testField(2, 2, Stone.BLACK);
    	boardNine.testField(1, 3, Stone.BLACK);
    	boardNine.testField(2, 3, Stone.BLACK);
    	assertEquals(8, boardNine.getLiberties(1, 2, Stone.BLACK, new HashSet<>()).size());
    }
    
    @Test
    public void testChainLiberties() {
    	boardNine.testField(1, 1, Stone.BLACK);
    	boardNine.testField(1, 2, Stone.BLACK);
    	boardNine.testField(3, 2, Stone.BLACK);
    	boardNine.testField(3, 3, Stone.BLACK);
    	assertEquals(6, boardNine.getLiberties(1, 2, Stone.BLACK, new HashSet<>()).size());
    	boardNine.testField(2, 2, Stone.BLACK);
    	assertEquals(10, boardNine.getLiberties(1, 2, Stone.BLACK, new HashSet<>()).size());
    }
    
    @Test
    public void testChains() {
    	boardNine.testField(1, 1, Stone.BLACK);
    	boardNine.testField(1, 2, Stone.BLACK);
    	boardNine.testField(3, 2, Stone.BLACK);
    	boardNine.testField(3, 3, Stone.BLACK);
    	assertEquals(2, boardNine.getChain(1, 1, Stone.BLACK, new HashSet<>()).size());
    	boardNine.testField(2, 2, Stone.BLACK);
    	assertEquals(5, boardNine.getChain(1, 2, Stone.BLACK, new HashSet<>()).size());
    }
    
    @Test
    public void testNeighbours() {
    	boardFive.testField(0, 0, Stone.BLACK);
    	assertEquals(2, boardFive.getNeighbours(boardFive.getChain(0, 0, Stone.BLACK, new HashSet<>())).size());
    	boardFive.testField(3, 3, Stone.BLACK);
    	assertEquals(4, boardFive.getNeighbours(boardFive.getChain(3, 3, Stone.BLACK, new HashSet<>())).size());
    	boardFive.testField(2, 3, Stone.BLACK);
    	assertEquals(6, boardFive.getNeighbours(boardFive.getChain(3, 3, Stone.BLACK, new HashSet<>())).size());
    }
    
    @Test
    public void testRemove() {
    	boardFive.testField(0, 0, Stone.BLACK);
    	boardFive.testField(0, 1, Stone.WHITE);
    	assertEquals(Stone.BLACK, boardFive.getField(0, 0));
    	boardFive.testField(1, 0, Stone.WHITE);
    	assertEquals(Stone.EMPTY, boardFive.getField(0, 0));

    }
    
    @Test
    public void testRemoveChains() {
    	boardNine.testField(1, 1, Stone.BLACK);
    	boardNine.testField(1, 2, Stone.BLACK);
    	boardNine.testField(2, 2, Stone.BLACK);
    	boardNine.testField(2, 3, Stone.BLACK);
    	boardNine.testField(1, 0, Stone.WHITE);
    	boardNine.testField(0, 1, Stone.WHITE);
    	boardNine.testField(0, 2, Stone.WHITE);
    	boardNine.testField(1, 3, Stone.WHITE);
    	boardNine.testField(2, 1, Stone.WHITE);
    	boardNine.testField(2, 4, Stone.WHITE);
    	boardNine.testField(3, 2, Stone.WHITE);
    	boardNine.testField(3, 3, Stone.WHITE);
    	assertEquals(Stone.EMPTY, boardNine.getField(2, 2));
    }
    
    @Test
    public void testRemoveSelfSuicide() {
    	boardFive.testField(0, 0, Stone.BLACK);
    	boardFive.testField(2, 0, Stone.WHITE);
    	boardFive.testField(0, 1, Stone.WHITE);
    	boardFive.testField(1, 1, Stone.WHITE);
    	boardFive.testField(1, 0, Stone.BLACK);
    	assertEquals(Stone.EMPTY, boardFive.getField(0, 0));
    	assertEquals(Stone.EMPTY, boardFive.getField(1, 0));
    }
    
    @Test
    public void testUsefullSuicide() {
    	//Outer circle
    	boardFive.testField(2, 0, Stone.BLACK);
    	boardFive.testField(1, 1, Stone.BLACK);
    	boardFive.testField(3, 1, Stone.BLACK);
    	boardFive.testField(0, 2, Stone.BLACK);
    	boardFive.testField(4, 2, Stone.BLACK);
    	boardFive.testField(1, 3, Stone.BLACK);
    	boardFive.testField(3, 3, Stone.BLACK);
    	boardFive.testField(2, 4, Stone.BLACK);
    	//Inner Circle
    	boardFive.testField(2, 1, Stone.WHITE);
    	boardFive.testField(1, 2, Stone.WHITE);
    	boardFive.testField(3, 2, Stone.WHITE);
    	boardFive.testField(2, 3, Stone.WHITE);

    	boardFive.testField(1, 0, Stone.BLACK);
    	assertEquals(Stone.BLACK, boardFive.getField(2, 2));
    	assertEquals(Stone.EMPTY, boardFive.getField(1, 2));

    }
    
    @Test //TODO: implement Ko in de game
    public void testPlacement() {
    	boardFive.testField(0, 1, Stone.WHITE);
    	boardFive.testField(1, 0, Stone.WHITE);
    	assertFalse(boardFive.isAllowed(0, 0, Stone.BLACK));
    	assertTrue(boardFive.isAllowed(0, 0, Stone.WHITE));
    	boardFive.testField(0, 0, Stone.BLACK);
    	assertEquals(Stone.EMPTY, boardFive.getField(0, 0));
    	assertFalse(boardFive.isAllowed(0, 1, Stone.BLACK));
    }
}
