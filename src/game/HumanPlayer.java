package game;

import java.io.*;

public class HumanPlayer extends Player {
	
	public HumanPlayer(String name, Stone stone) {
		super(name, stone);
	}
	
	public int determineMove(Board board) {
        String prompt = "> " + getName() + " (" + getColor().toString() + ")"
                + ", what is your choice? Please put: x y!";
        int choice = readChoice(prompt, board);
        int[] coor = board.coordinate(choice);
        boolean valid = board.isAllowed(choice, getColor());
        while (!valid) {
            System.out.println("ERROR: x " + coor[0] + " y " + coor[1]
                    + " is no valid choice.");
            choice = readChoice(prompt, board);
            valid = board.isAllowed(choice, getColor());
        }
        return choice;
    }

    private int readChoice(String prompt, Board board) {
    	int x = -1;
    	int y = -1;
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(prompt);
        try {
        	String input = line.readLine();
        	String[] splittedInput = splitString(input);
        	x = Integer.parseInt(splittedInput[0]);
        	y = Integer.parseInt(splittedInput[1]);
        } catch (IOException e) {
        	System.out.println("There is nothing here");
        }
        
        int value = board.index(x, y);
        return value;
    }
    
    public String[] splitString(String s) {
    	return s.split(" ");
    }
}
