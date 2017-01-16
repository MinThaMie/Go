package game;

public class RandomStrategy implements Strategy {
	String name;
	public RandomStrategy() {
		this.name = "Random";
	}

	public String getName() {
		return this.name;
	}
	
	public int determineMove(Board b, Stone s) {
		return (int) Math.random();
	}
}
