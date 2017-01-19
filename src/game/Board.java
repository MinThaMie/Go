package game;
import java.util.*;
import com.nedap.go.gui.GoGUIIntegrator;

public class Board {
	
	private final int dim;
	private final Stone[] fields;
	GoGUIIntegrator gui;
	public Board(int dim) {
		this.dim = dim;
    	fields = new Stone[dim * dim];
		for (int i = 0; i < dim * dim; i++) {
			setField(i, Stone.EMPTY);
		}
	}
	
	public Board(int dim, GoGUIIntegrator gogui) {
		this(dim);
		this.gui = gogui;
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
		return getLiberties(x, y, s, new HashSet<>(), new HashSet<>()).size() > 0;
	}
	
	public Set<Integer> getChain(int x, int y, Stone s, Set<Integer> set) {
		Set<Integer> chain = new HashSet<>();
		chain.addAll(set);
		Set<Integer> neighbours = getNeighbours(x, y);
		chain.add(index(x, y));
		for (int i : neighbours) {
			if (!chain.contains(i) && getField(i) == s) {
				int[] coor = coordinate(i);
				chain.addAll(getChain(coor[0], coor[1], s, chain));
			}
		}
		return chain;
	}
	
	/**
	 * Calculates the indices of the liberties of a stone. 
	 * Only horizontally and vertically, not diagonally.
	 * If the stone chains with another stone it calls the method getChainLiberties.
	 * @return a set with the indices that are the liberties of a certain stone
	 */
	public Set<Integer> getLiberties(int x, int y, Stone s, Set<Integer> liberties, Set<Integer> chain ) {
		liberties.addAll(liberties);
		chain.addAll(chain);
		int pos = index(x, y);
		Set<Integer> neighbours = getNeighbours(x, y);
		for (int i : neighbours) {
			if (getField(i) == Stone.EMPTY) {
				chain.add(pos);
				liberties.add(i);
			} else if (!chain.contains(i) && getField(i) == s) {
				int[] coor = coordinate(i);
				chain.add(pos);
				liberties.addAll(getLiberties(coor[0], coor[1], s, liberties, chain));
			}
		}
		return liberties;
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
		int[] coor = coordinate(i);
		setField(coor[0], coor[1], s);
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
		if (s == Stone.EMPTY) { // For the setup
			fields[index] = s;
		} else if (isAllowed(x, y, s)) {
			fields[index] = s;
			getChain(x, y, s, new HashSet<>());
			Set<Integer> neighbours = getNeighbours(x, y);
			for (int i : neighbours) {
				int[] coor = coordinate(i);
				if (getField(i) != Stone.EMPTY) {
					if (getLiberties(coor[0], coor[1], getField(i), new HashSet<>(), new HashSet<>()).isEmpty()) {
						System.out.println("Should remove " + coor[0] + " " + coor[1]);
						remove(coor[0], coor[1], getField(i));
					}
				}
			}
		}
	}
	
	public void remove(int x, int y, Stone s) {
		Set<Integer> toBeRemoved = getChain(x, y, s, new HashSet<>());
		for (int i : toBeRemoved) {
			fields[i] = Stone.EMPTY;
			int[] coor = coordinate(i);
			gui.removeStone(coor[0], coor[1]); //disable during test
		}
	}
	
	public boolean isAllowed(int x, int y, Stone s) {
		return isEmpty(x, y) && !getLiberties(x, y, s, new HashSet<>(), new HashSet<>()).isEmpty();
	}
// Neighbours	
	public Set<Integer> getNeighbours(int x, int y) {
		Set<Integer> neighbours = new HashSet<>();
		if (getTopNeighbour(x, y) >= 0) {
			neighbours.add(getTopNeighbour(x, y));
		}
		if (getBottomNeighbour(x, y) >= 0) {
			neighbours.add(getBottomNeighbour(x, y));
		}
		if (getLeftNeighbour(x, y) >= 0) {
			neighbours.add(getLeftNeighbour(x, y));
		}
		if (getRightNeighbour(x, y) >= 0) {
			neighbours.add(getRightNeighbour(x, y));
		}
		return neighbours;
	}
	public int getTopNeighbour(int x, int y) {
		return (y - 1 >= 0) ? index(x, y - 1) : -1;
	}
	public int getBottomNeighbour(int x, int y) {
		return (y + 1 < dim) ? index(x, y + 1) : -1;
	}
	public int getLeftNeighbour(int x, int y) {
		return (x - 1 >= 0) ? index(x - 1, y) : -1;
	}
	public int getRightNeighbour(int x, int y) {
		return  (x + 1 < dim) ? index(x + 1, y) : -1;
	}
}
