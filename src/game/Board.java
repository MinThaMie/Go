package game;
import java.util.*;

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
	/**
	 * Returns whether an index has liberties on the board.
	 */
	public boolean hasLiberties(int i, Stone s) {
		int[] coor = coordinate(i);
		return hasLiberties(coor[0], coor[1], s);
	}
	/**
	 * Returns whether an coordinate has liberties on the board.
	 */
	public boolean hasLiberties(int x, int y, Stone s) {
		return getLiberties(x, y, s).size() > 0;
	}
	/**
	 * Calculates the indices of the liberties of a stone. 
	 * Only horizontally and vertically, not diagonally.
	 * If the stone chains with another stone it calls the method getChainLiberties.
	 * @return a set with the indices that are the liberties of a certain stone
	 */
	public Set<Integer> getLiberties(int x, int y, Stone s) {
		Set<Integer> libertyList = new HashSet<>();
		for (int i = x - 1; i <= x + 1; i++) {
			if (i >= 0 && i != x) { //ignore the stone and respect the edges
				if (isEmpty(i, y)) {
					libertyList.add(index(i, y));

				} else if (getField(i, y) == s) {
					Set<Integer> chains = getChainLiberties(i, y, s, x, y);
					libertyList.addAll(chains);
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != y) { //ignore the stone and respect the edges
				if (isEmpty(x, j)) {
					libertyList.add(index(x, j));

				} else if (getField(x, j) == s) {
					Set<Integer> chains = getChainLiberties(x, j, s, x, y);
					libertyList.addAll(chains);
				}

			}

		}
		return libertyList;
	}
	/**
	 * This method works similar to the getLiberties method. 
	 * It does not check the previous stone for liberties. 
	 * This works recursively in case that the chain is longer than just one stone.
	 * @param prevX
	 * @param prevY
	 */
	private Set<Integer> getChainLiberties(int x, int y, Stone s, int prevX, int prevY) {
		Set<Integer> chainLibertyList = new HashSet<>();
		for (int i = x - 1; i <= x + 1; i++) {
			if (i >= 0 && i != x && i != prevX) {
				if (isEmpty(i, y)) {
					chainLibertyList.add(index(i, y));
				} else if (getField(i, y) == s) {
					chainLibertyList.addAll(getChainLiberties(i, y, s, x, y));
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != y && j != prevY) {
				if (isEmpty(x, j)) {
					chainLibertyList.add(index(x, j));
				} else if (getField(x, j) == s) {
					chainLibertyList.addAll(getChainLiberties(x, j, s, x, y));
				}

			}

		}
		return chainLibertyList;
	}
		
	int index(int x, int y) {
		return x + y * dim;
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
