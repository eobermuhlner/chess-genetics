package ch.obermuhlner.genetic.chess.engine;

import java.util.List;
import java.util.Random;

public class MonteCarloChessEngine {

	private final Random random = new Random();

	public double evaluatePosition(Board board) {
		return board.getValue();
	}

	public double evaluatePosition(Board board, int gameCount, int moveCount) {
		double totalValue = 0;
		for (int game = 0; game < gameCount; game++) {
			double value = evaluateGame(board.clone(), moveCount);
			totalValue += value;
		}
		
		return totalValue / gameCount;
	}

	public double evaluatePlaying(Board board, int gameCount, int moveCount) {
		int whiteWins = 0;
		int blackWins = 0;
		
		for (int game = 0; game < gameCount; game++) {
			Side winner = playGame(board.clone(), moveCount);
			if (winner == Side.White) {
				whiteWins++;
			}
			if (winner == Side.Black) {
				blackWins++;
			}
		}
		
		return (double)(whiteWins - blackWins) / gameCount;
	}

	private Side playGame(Board board, int moveCount) {
		for (int i = 0; i < moveCount; i++) {
			List<Move> allMoves = board.getAllMoves();
			if (allMoves.isEmpty()) {
				return board.getSideToMove().otherSide();
			}
			board.move(randomMove(allMoves));
		}
		
		double value = board.getValue();
		if (value > 0) {
			return Side.White;
		}
		if (value < 0) {
			return Side.Black;
		}
		
		return null;
	}

	private double evaluateGame(Board board, int moveCount) {
		for (int i = 0; i < moveCount; i++) {
			List<Move> allMoves = board.getAllMoves();
			if (allMoves.isEmpty()) {
				break;
			}
			board.move(randomMove(allMoves));
		}
		
		return board.getValue();
	}

	private Move randomMove(List<Move> allMoves) {
		if (allMoves.isEmpty()) {
			return null;
		}
		
		double total = 0;
		for (Move move : allMoves) {
			total += move.getValue();
		}
		
		double r = random.nextDouble() * total;
		
		total = 0;
		for (Move move : allMoves) {
			total += move.getValue();
			if (r < total) {
				return move;
			}
		}

		return allMoves.get(allMoves.size() - 1);
	}

	public static void main(String[] args) {
		MonteCarloChessEngine chessEngine = new MonteCarloChessEngine();
		
		Board board = new Board();
		board.setFenString("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR");
		
		double value = chessEngine.evaluatePlaying(board, 1000, 100);
		System.out.println("VALUE " + value);
	}
}
