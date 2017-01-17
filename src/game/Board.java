package game;

public class Board {
	
	private final int dim;
	private final Stone[] fields;
	public Board(int dim) {
		this.dim = dim;
    	fields = new Stone[dim * dim];
		for (int i = 0; i < dim * dim; i++) {
			setField(i, Stone.EMPTY);
		}
	}
	//Getters TODO: correct name?
	/**
	 * This function tests whether a index is a field on the board.
	 * @param i: index of the queried field
	 * @return boolean true if the field is on the board
	 */
	public boolean isField(int i) {
		return i >= 0 && i < fields.length;
	}
	//TODO: Find out what common practice is with these kind of functions
	// overloading a function or using the index function
	public boolean isField(int x, int y) {
		int index = index(x, y);
		return index >= 0 && index < fields.length;
	}
	/**
	 * This function checks whether a field is empty or not.
	 * @param i: index of the queried field
	 * @return boolean true if the field is empty
	 */
	public boolean isEmpty(int i) {
		return isField(i) && fields[i] == Stone.EMPTY;
	}
	
	public boolean isEmpty(int x, int y) {
		int i = index(x, y);
		return isEmpty(i);
	}
		
	int index(int x, int y) {
		return x * dim + y;
	}
	
	public int[] coordinate(int i) {
		int y = i / dim;
		int x = i % dim;
		int[] coordinate = {x, y};
		return coordinate;
	}
	
	public int getBoardSize() {
		return this.dim;
	}
	
	public Stone getField(int i) {
		return isField(i) ? fields[i] : null;
	}
	
	public Stone getField(int x, int y) {
		return fields[index(x, y)];
	}
	
	//Setters TODO: correct name?
	/**
	 * This function sets a field on the board to the provided stone.
	 * This function is package private.
	 * @param i: index of the field
	 * @param s: the stone placed
	 */
	public void setField(int i, Stone s) {
		if (isField(i)) {
			fields[i] = s;
		}
	}
	/**
	 * This function overloads the function above does the same.
	 * Uses the index-function to determine the index of the requested point.
	 * @param x: x-coordinate
	 * @param y: y-coordinate
	 * @param s: the stone placed
	 */
	public void setField(int x, int y, Stone s) {
		int index = index(x, y);
		if (isField(index)) {
			fields[index] = s;
		}
	}
}
