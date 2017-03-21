package ch.obermuhlner.genetic.chess.engine;

import java.util.List;
import java.util.Random;

import ch.obermuhlner.genetic.chess.engine.Board.Move;

public class RandomChessEngine {

	private static final int MAX_MOVE_COUNT = 200;
	
	private final Random random = new Random();
	
	public double evaluate(Board board, int gameCount) {
		int whiteWins = 0;
		int blackWins = 0;
		
		for (int game = 0; game < gameCount; game++) {
			Board localBoard = board.clone();
			int moveCount = 0;
			do {
				List<Move> allMoves = localBoard.getAllMoves();
				Move move = randomMove(allMoves);
				if (move == null) {
					break;
				}
				localBoard.move(move);
				moveCount++;
				if (moveCount > MAX_MOVE_COUNT) {
					double value = board.getValue();
					System.out.println(value);
					if (value > 0) {
						whiteWins++;
					}
					if (value < 0) {
						blackWins++;
					}
					break;
				}
			} while (true);
			
			if (board.isMate()) {
				if (board.getSideToMove() == Side.White) {
					blackWins++;
				} else {
					whiteWins++;
				}
			}
		}
		
		return (double)(whiteWins - blackWins) / gameCount;
	}

	private Move randomMove(List<Move> allMoves) {
		if (allMoves.isEmpty()) {
			return null;
			
		}
		
		return allMoves.get(random.nextInt(allMoves.size()));
	}
	
	public static void main(String[] args) {
		RandomChessEngine chessEngine = new RandomChessEngine();
		
		Board board = new Board();
		board.setStartPosition();
		
		double value = chessEngine.evaluate(board, 100);
		System.out.println("VALUE " + value);
	}
}
