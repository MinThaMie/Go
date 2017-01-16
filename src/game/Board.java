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
	
	private boolean isField(int i) {
		return i > 0 && i < fields.length;
	}
	
	private boolean isEmpty(int i) {
		return fields[i] == Stone.EMPTY;
	}
	
	private int index(int x, int y) {
		return x * dim + y;
	}
	
	//------Setters------
	/**
	 * This function sets a field on the board to the provided stone.
	 * This function is package private.
	 * @param i: index of the field
	 * @param s: the stone placed
	 */
	void setField(int i, Stone s) {
		if (isField(i)) {
			fields[i] = s;
		}
		System.out.println(i + ": " + fields[i]);
	}
	/**
	 * This function overloads the function above does the same.
	 * Uses the index-function to determine the index of the requested point.
	 * @param x: x-coordinate
	 * @param y: y-coordinate
	 * @param s: the stone placed
	 */
	void setField(int x, int y, Stone s) {
		int index = index(x, y);
		if (isField(index)) {
			fields[index] = s;
		}
	}
}
