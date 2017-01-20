package game;

public class ComputerPlayer extends Player {
	Stone color;	
	Strategy strategy;
	public ComputerPlayer(Strategy strategy, Stone stone) {
		super(strategy + "-" + stone, stone);
		this.color = color;
		this.strategy = strategy;
	}
	
	public ComputerPlayer(Stone stone) {
		super("random" + "-" + stone, stone);
		Strategy random = new RandomStrategy();
		this.strategy = random;
	}
	
	public int determineMove(Board board) {
		return strategy.determineMove(board, this.color);
	}
}
