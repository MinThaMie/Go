package player;

import java.util.*;

import game.Board;
import game.Stone;


public class RandomStrategy implements Strategy {
	String name;
	public RandomStrategy() {
		this.name = "random";
	}

	public String getName() {
		return this.name;
	}
	
	public int determineMove(Board b, Stone s) {
		List<Integer> emptyFields = new LinkedList<Integer>();
		for (int i = 0; i < b.getBoardSize() * b.getBoardSize(); i++) {
			if (b.isEmpty(i)) {
				emptyFields.add(i);
			}
		}

		int size = emptyFields.size();
		if (emptyFields.size() > 0) {
			double random = Math.random();
			int index = (int) (random * size);
			return emptyFields.get(index);
		}  else {
			return -1;
		}
	}
}
