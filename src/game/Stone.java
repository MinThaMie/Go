package game;

import java.util.HashSet;
import java.util.Set;

import com.nedap.go.gui.GoGUIIntegrator;

/**
 * A Go stone can be either black or white and there could be no stone at all (EMPTY).
 */
//TODO: Update Documentation

public class Stone {
	private StoneColor color;
	private Set<Integer> liberties;
	private boolean inDanger;
	public Stone(StoneColor color) {
		this.color = color;
		this.liberties = null;
		this.inDanger = false;
	}

	public enum StoneColor {
		EMPTY, BLACK, WHITE;
	}
	
	public StoneColor getColor() {
		return this.color;
	}
	/**
	 * This function returns the other stone, if something is Stone.EMPTY it will return EMPTY.
	 */
	public StoneColor other() {
		if (this.color == StoneColor.BLACK) {
			return StoneColor.WHITE;
		} else if (this.color == StoneColor.WHITE) {
			return StoneColor.BLACK;
		} else {
			return StoneColor.EMPTY;
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
		return checkLiberties(x, y, b).size() > 0;
	}
	/**
	 * Calculates the indices of the liberties of a stone. 
	 * Only horizontally and vertically, not diagonally.
	 * If the stone chains with another stone it calls the method getChainLiberties.
	 * @return a set with the indices that are the liberties of a certain stone
	 */
	
	public Set<Integer> getLiberties() {
		return this.liberties;
	}
	
	public void setLiberties(int x, int y, Board b) {
		this.liberties = checkLiberties(x, y, b);
		this.inDanger = liberties.size() == 1;
	}
	
	public boolean inDanger() {
		return this.inDanger;
	}
	
	public boolean checkInDanger(int x, int y, Board b) {
		return checkLiberties(x, y, b).size() == 1;
	}
	
	public Set<Integer> checkLiberties(int x, int y, Board b) {
		Set<Integer> libertyList = new HashSet<>();
		for (int i = x - 1; i <= x + 1; i++) {
			if (i >= 0 && i != x && i < b.getBoardSize()) { //ignore the stone and respect the edges
				if (b.isEmpty(i, y)) {
					libertyList.add(b.index(i, y));

				} else if (b.getField(i, y).getColor() == this.getColor()) {
					Set<Integer> chains = getChainLiberties(i, y, b, x, y);
					libertyList.addAll(chains);
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != y && j < b.getBoardSize()) { //ignore the stone and respect the edges
				if (b.isEmpty(x, j)) {
					libertyList.add(b.index(x, j));

				} else if (b.getField(x, j).getColor() == this.getColor()) {
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
			if (i >= 0 && i != x && i != prevX && i < b.getBoardSize()) {
				if (b.isEmpty(i, y)) {
					chainLibertyList.add(b.index(i, y));
				} else if (b.getField(i, y).getColor() == this.getColor()) {
					chainLibertyList.addAll(getChainLiberties(i, y, b, x, y));
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != y && j != prevY && j < b.getBoardSize()) {
				if (b.isEmpty(x, j)) {
					chainLibertyList.add(b.index(x, j));
				} else if (b.getField(x, j).getColor() == this.getColor()) {
					chainLibertyList.addAll(getChainLiberties(x, j, b, x, y));
				}

			}

		}
		return chainLibertyList;
	}

	public void checkNeighbours(int x, int y, Board b, GoGUIIntegrator gogui) {
		System.out.println("Checking the neigbours");

		for (int i = x - 1; i <= x + 1; i++) {
			if (i >= 0 && i != x && i < b.getBoardSize()) {
				Stone neighbour = b.getField(i);
				
				neighbour.setLiberties(i, y, b);
				if (neighbour.getLiberties().size() == 0) {
					neighbour.remove();
					gogui.removeStone(i, y);
				}
			}
		}
		for (int j = y - 1; j <= y + 1; j++) {
			if (j >= 0 && j != x && j < b.getBoardSize()) {
				Stone neighbour = b.getField(j);
				neighbour.setLiberties(x, j, b);

				if (neighbour.getLiberties().size() == 0) {
					neighbour.remove();
					gogui.removeStone(x, j);
				}
			}
		}
	}
	
	
	/**
	 * Remove the stone from the board and remove it's liberties!
	 */
	public void remove() {
		this.color = StoneColor.EMPTY;
		this.liberties = null;
		this.inDanger = false;
	}
}
