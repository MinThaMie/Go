package game;

import java.util.HashSet;
import java.util.Set;

/**
 * A Go stone can be either black or white and there could be no stone at all (EMPTY).
 */

public enum Stone {

	EMPTY, BLACK, WHITE;
	
	/**
	 * This function returns the other stone, if something is Stone.EMPTY it will return EMPTY.
	 */
	public Stone other() {
		if (this == BLACK) {
			return WHITE;
		} else if (this == WHITE) {
			return BLACK;
		} else {
			return EMPTY;
		}
	}
	/**
	 * Returns whether an index has liberties on the board.
	 */
	public boolean hasLiberties(int i, Board b) {
		int[] coor = b.coordinate(i);
		return hasLiberties(coor[0], coor[1], b);
	}
	/**
	 * Returns whether an coordinate has liberties on the board.
	 */
	public boolean hasLiberties(int x, int y, Board b) {
		return getLiberties(x, y, b).size() > 0;
	}
	/**
	 * Calculates the indices of the liberties of a stone. 
	 * Only horizontally and vertically, not diagonally.
	 * If the stone chains with another stone it calls the method getChainLiberties.
	 * @return a set with the indices that are the liberties of a certain stone
	 */
	public Set<Integer> getLiberties(int x, int y, Board b) {
		Set<Integer> libertyList = new HashSet<>();
		for (int i = x - 1; i <= x + 1; i++) {
			if (i >= 0 && i != x) { //ignore the stone and respect the edges
				if (b.isEmpty(i, y)) {
					libertyList.add(b.index(i, y));

				} else if (b.getField(i, y) == this) {
					Set<Integer> chains = getChainLiberties(i, y, b, x, y);
					libertyList.addAll(chains);
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != y) { //ignore the stone and respect the edges
				if (b.isEmpty(x, j)) {
					libertyList.add(b.index(x, j));

				} else if (b.getField(x, j) == this) {
					Set<Integer> chains = getChainLiberties(x, j, b, x, y);
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
	private Set<Integer> getChainLiberties(int x, int y, Board b, int prevX, int prevY) {
		Set<Integer> chainLibertyList = new HashSet<>();
		for (int i = x - 1; i <= x + 1; i++) {
			if (i >= 0 && i != x && i != prevX) {
				if (b.isEmpty(i, y)) {
					chainLibertyList.add(b.index(i, y));
				} else if (b.getField(i, y) == this) {
					chainLibertyList.addAll(getChainLiberties(i, y, b, x, y));
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != y && j != prevY) {
				if (b.isEmpty(x, j)) {
					chainLibertyList.add(b.index(x, j));
				} else if (b.getField(x, j) == this) {
					chainLibertyList.addAll(getChainLiberties(x, j, b, x, y));
				}

			}

		}
		return chainLibertyList;
	}
}
