package game;

import java.io.*;

public class HumanPlayer extends Player {
	public static final String EXIT = "exit";
	public static final String SURRENDER = "surrender";

	public static final String PASS = "pass";
	public static final String MOVE = "move";

	
	public HumanPlayer(String name, Stone stone) {
		super(name, stone);
	}
	
	public void takeTurn(Board board) {
		String prompt = "> " + getName() + " (" + getColor().toString() + ")"
                + ", what do you want to do this turn? Move, pass or surrender!";
		String command = readCommand(prompt);
		if (command.equals(PASS)) {
			System.out.println("You passed");
		} else if (command.equals(EXIT) || command.equals(SURRENDER)) {
			System.out.println("You wanna go");
		} else if (command.equals(MOVE)) {
			makeMove(board);
		} else {
			System.out.println("You did not provide a valid command, please try again!");
			takeTurn(board);
		}
	}
	
	public String readCommand(String prompt) {
		String command = "NO";
		String input;
		BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(prompt);
        try {
        	input = line.readLine();
        	String[] splittedInput = splitString(input);
        	command = splittedInput[0].toLowerCase();
        } catch (IOException e) {
        	System.out.println("There is nothing here");
        }
        return command;
	}
	
	
	public int determineMove(Board board) {
        String prompt = "> " + getName() + " (" + getColor().toString() + ")"
                + ", what is your choice? Please put: x y!";
        int move = readMove(prompt, board);
        int[] coor = board.coordinate(move);
        System.out.println("x" + coor[0] + "y" + coor[1] + " choice " + move);

        boolean valid = isAllowed(board, coor[0], coor[1], getColor());
        while (!valid) {
            System.out.println("ERROR: x " + coor[0] + " y " + coor[1]
                    + " is no valid choice.");
            move = readMove(prompt, board);
            coor = board.coordinate(move);
            valid = isAllowed(board, coor[0], coor[1], getColor());
        }
        return move;
    }

    private int readMove(String prompt, Board board) {
    	int x = -1;
    	int y = -1;
    	BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(prompt);
        try {
        	String input = line.readLine();
        	String[] splittedInput = splitString(input);
        	if (splittedInput[0].equals(EXIT)) {
        		System.out.println("Wanna get out bro");
        	} else {
	        	try {
		        	x = Integer.parseInt(splittedInput[0]);
		        	y = Integer.parseInt(splittedInput[1]);
	        	} catch (NumberFormatException e) {
	        		System.out.println("You did not use integers to send your coordinate. Please input: int int!");
	        		readMove(prompt, board);
	        	}
        	}
        } catch (IOException e) {
        	System.out.println("There is nothing here");
        }
        
        int value = board.index(x, y);
        System.out.println("x" + x + "y" + y + " value " + value);
        return value;
    }
    
    public String[] splitString(String s) {
    	return s.split(" ");
    }
}
