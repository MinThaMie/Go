package hersenspinsels;

public class Hersenspinsel {
	if (x == 0 || x == getBoardSize() - 1 ||  y == 0 || y == getBoardSize() - 1) {
		if (x == 0 && y == 0) {
			return getField(x + 1, y) == s || getField(x, y + 1) == s || 
					isEmpty(x + 1, y) || isEmpty(x, y + 1);
		} else if (x == 0 && y == getBoardSize() - 1) {
			return getField(x + 1, y) == s || getField(x, y - 1) == s ||
				isEmpty(x + 1, y) || isEmpty(x, y - 1);
		} else if (x == getBoardSize() - 1 && y == 0) {
			return getField(x - 1, y) == s || getField(x, y + 1) == s ||
					isEmpty(x - 1, y) || isEmpty(x, y + 1);
		} else if (x == getBoardSize() - 1 && y == getBoardSize() - 1) {
			return getField(x - 1, y) == s || getField(x, y - 1) == s ||
					isEmpty(x - 1, y) || isEmpty(x, y - 1);
		} else if (x == 0) {
			return getField(x + 1, y) == s || getField(x, y - 1) == s 
					|| getField(x, y + 1) == s || isEmpty(x + 1, y) || isEmpty(x, y - 1) 
							|| isEmpty(x, y + 1);
		} else if (x == getBoardSize() - 1) {
			return getField(x - 1, y) == s || getField(x, y - 1) == s 
					|| getField(x, y + 1) == s || isEmpty(x - 1, y) || isEmpty(x, y - 1) 
							|| isEmpty(x, y + 1);
		} else if (y == 0) {
			return getField(x + 1, y) == s || getField(x - 1, y) == s 
					|| getField(x, y + 1) == s || isEmpty(x + 1, y) || isEmpty(x - 1, y) 
							|| isEmpty(x, y + 1);
		} else if (y == getBoardSize() - 1) {
			return getField(x + 1, y) == s || getField(x - 1, y) == s 
					|| getField(x, y - 1) == s || isEmpty(x + 1, y) || isEmpty(x - 1, y)  
					|| isEmpty(x, y - 1); 
		}  else {
			System.out.println("Woooohooo I'm unique!");
			return false; // DOES NOT HAPPEN
		}
	} else {
		return getField(x - 1, y) == s || getField(x + 1, y) == s ||
				getField(x, y + 1) == s || getField(x, y - 1) == s || isEmpty(x - 1, y) ||
				isEmpty(x + 1, y) || isEmpty(x, y + 1) || isEmpty(x, y - 1);
	}
}
