package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import communication.ClientHandler.Keyword;

public class NetworkPlayer extends Player {
	private int xCoor;
	private int yCoor;
	
	public NetworkPlayer(String name, Stone stone) {
		super(name, stone);
	}
	
	public void takeTurn(Board board) {
		
	}
	
	public void takeTurn(Board board, String choice) {
		String[] commandParts = splitString(choice);
		System.out.println("command " + commandParts[0]);
		if (commandParts[0].equals(Keyword.PASS)) {
			System.out.println("You passed");
		} else if (commandParts[0].equals(Keyword.TABLEFLIP)) {
			System.out.println("You wanna go"); //TODO: validate this choice && implement
		} else if (commandParts[0].equals(Keyword.MOVE.toString())) {
			if (validateMove(board, commandParts[1], commandParts[2])) {
				makeMove(board);
				System.out.println("Off to the serer....");
			} else {
				System.out.println("Apperently your move is not valid!");
			}
		} else {
			System.out.println("You did not provide a valid command, please try again!");
			takeTurn(board);
		}
	}
	
	public int determineMove(Board board) {
		return board.index(xCoor, yCoor);
	}
	
	public boolean validateMove(Board board, String x, String y) {
        System.out.println("x" + x + "y" + y);
        try {
        	xCoor = Integer.parseInt(x);
        	yCoor = Integer.parseInt(y);
        } catch (NumberFormatException e) {
        	System.out.println("You did not use integers to send your coordinate!");
        	return false;
        }
        return isAllowed(board, xCoor, yCoor, getColor());
    }
	
	public String[] readString(String prompt) {
		String input;
		String[] splittedInput = null;
		System.out.println(prompt);
		BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
        try {
        	input = line.readLine();
        	splittedInput = splitString(input);
        	System.out.println("array " + Arrays.toString(splittedInput));
        } catch (IOException e) {
        	System.out.println("There is nothing here");
        }
        return splittedInput;
	}
    
    public String[] splitString(String s) {
    	return s.split(" ");
    }
}
