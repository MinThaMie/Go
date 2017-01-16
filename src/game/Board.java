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
	
	private void setField(int i, Stone s) {
		if (isField(i)) {
			fields[i] = s;
		}
		System.out.println(i + ": " + fields[i]);
	}
	
	private void setField(int x, int y, Stone s) {
		int index = index(x, y);
		if (isField(index)) {
			fields[index] = s;
		}
	}
	
	public static void main(String[] args) {
		Board b = new Board(5);
		b.setField(3, Stone.BLACK);
		b.setField(1, 1, Stone.WHITE);
	}
}
