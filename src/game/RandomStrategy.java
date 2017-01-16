package game;

public class RandomStrategy implements Strategy {
	String name;
	public RandomStrategy() {
		this.name = "Random";
	}

	public String getName() {
		return this.name;
	}
	//TODO: Fix the infinite loop
	public int determineMove(Board b, Stone s) {
		double random = Math.random();
		int index = (int) (random * b.getBoardSize());
		while (!b.isEmpty(index)) {
			random = Math.random();
			index = (int) (random * b.getBoardSize());
		} 
		return index;
	}
}
