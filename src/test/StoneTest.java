package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.Stone;
import game.Stone.StoneColor;

public class StoneTest {
	private Board boardFive;
	private Board boardNine;
	private Stone sB;
	private Stone sW;
	@Before
    public void setUp() {
        boardFive = new Board(5);
        boardNine = new Board(9); 
        sB = new Stone(StoneColor.BLACK);
        sW = new Stone(StoneColor.WHITE);
        
    }
	
	//Checkt stiekem alleen of er liberties zijn in als je hem neer wilt zetten, niet daadwerkelijk neergezet
	@Test
    public void testLibertiesCorner() {
    	assertTrue(sW.hasLiberties(0, 0, boardFive));
    	assertFalse(sW.checkInDanger(0, 0, boardFive));
    	boardFive.setField(0, 1, sB);
    	assertTrue(sW.hasLiberties(0, 0, boardFive));
    	assertTrue(sW.checkInDanger(0, 0, boardFive));
    	boardFive.setField(1, 0, sB);
    	assertFalse(sW.hasLiberties(0, 0, boardFive));
    }
   
    @Test
    public void testLibertiesEdge() {
    	assertTrue(sW.hasLiberties(1, 0, boardFive));
    	boardFive.setField(0, 0, sB);
    	assertTrue(sW.hasLiberties(1, 0,  boardFive));
    	boardFive.setField(2, 0, sB);
    	boardFive.setField(1, 1, sB);
    	assertFalse(sW.hasLiberties(1, 0,  boardFive));
    }
    
    @Test
    public void testLiberties() {
    	boardNine.setField(0, 2, sB);
    	boardNine.setField(1, 1, sB);
    	boardNine.setField(2, 2, sB);
    	boardNine.setField(1, 2, sW);
    	assertTrue(sW.hasLiberties(1, 2, boardNine));
    	assertTrue(sW.inDanger());
    	boardNine.setField(1, 3, sW);
    	assertTrue(sW.hasLiberties(1, 2, boardNine)); 
    }
    
    @Test
    public void testGetLiberties() {
    	boardNine.setField(0, 2, sB);
    	boardNine.setField(1, 1, sB);
    	assertEquals(4, sB.getLiberties().size());
    	boardNine.setField(2, 2, sB);    	
    	boardNine.setField(1, 2, sW);
    	assertEquals(1, sW.getLiberties().size());
    	boardNine.setField(1, 3, sW);
    	assertEquals(3, sW.getLiberties().size());
    	assertEquals(3, sW.getLiberties().size());
    	boardNine.setField(1, 4, sW);
    	boardNine.setField(2, 4, sW);
    	assertEquals(6, sW.getLiberties().size());
    }
}
