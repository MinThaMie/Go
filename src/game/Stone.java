package game;

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
}
