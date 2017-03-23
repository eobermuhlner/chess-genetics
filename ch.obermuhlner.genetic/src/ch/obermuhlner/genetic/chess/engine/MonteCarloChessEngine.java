package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MonteCarloChessEngine implements ChessEngine {

	private final Random random = new Random();

	private Board board;

	@Override
	public void setStartPosition() {
		board = new Board();
		board.setStartPosition();
	}

	@Override
	public void setFen(String fen) {
		board = new Board();
		board.setFenString(fen);
	}

	@Override
	public double evaluate() {
		return board.getValue();
	}

	@Override
	public String bestMove(long thinkMilliseconds) {
		Move move = getBestMove(board, thinkMilliseconds);
		if (move == null) {
			return null;
		}
		
		return move.getSource().getPositionString() + move.getTargetPositionString();
	}

	@Override
	public void move(String move) {
		
	}
	
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
	
	public Move getBestMove(Board board, long thinkMilliseconds) {
		List<Move> allMoves = board.getAllMoves();
		if (allMoves.isEmpty()) {
			return null;
		}
		
		List<MoveStatistic> moveStatistics = allMoves.stream()
			.map(move -> new MoveStatistic(move))
			.collect(Collectors.toList());
		
		return findBestMove(board, moveStatistics, thinkMilliseconds);
	}
	
	private Move findBestMove(Board board, List<MoveStatistic> moveStatistics, long thinkMilliseconds) {
		int moveCount = 200;
		long averagePlayMillis = 100;
		
		while (thinkMilliseconds > 0) {
			moveStatistics = reduceStatistics(moveStatistics, thinkMilliseconds, averagePlayMillis);
			
			long thinkStartMillis = System.currentTimeMillis();

			for (MoveStatistic moveStatistic : moveStatistics) {
				Board localBoard = board.clone();
				localBoard.move(moveStatistic.move);
				
				Side winningSide = playGame(localBoard, moveCount);

				moveStatistic.playCount++;
				if (winningSide == Side.White) {
					moveStatistic.whiteWins++;
				}
				if (winningSide == Side.Black) {
					moveStatistic.blackWins++;
				}
			}
			
			long thinkEndMillis = System.currentTimeMillis();
			thinkMilliseconds -= thinkEndMillis - thinkStartMillis;
			
			averagePlayMillis = thinkMilliseconds / moveStatistics.size();
		}
		
		return moveStatistics.get(0).move;
	}

	private List<MoveStatistic> reduceStatistics(List<MoveStatistic> moveStatistics, long thinkMilliseconds, long averagePlayMillis) {
		int currentSize = moveStatistics.size();
		int optimumSize;
		if (averagePlayMillis * currentSize * 2 < thinkMilliseconds) {
			optimumSize = currentSize;
		} else {
			optimumSize = Math.min(currentSize, Math.max(5, currentSize / 2));
		}
		
		moveStatistics.sort((move1, move2) -> {
			int compare = Double.compare(move1.getValue(), move2.getValue());
			if (compare == 0) {
				compare = Double.compare(move1.move.getValue(), move2.move.getValue());
			}
			return compare;
		});
		
		System.out.println("REDUCE " + currentSize + " to " + optimumSize + " moves : " + moveStatistics.get(0) + " with average " + averagePlayMillis + " ms");
		
		if (optimumSize == currentSize) {
			return moveStatistics;
		}
		return new ArrayList<>(moveStatistics.subList(0, optimumSize));
	}

	private static class MoveStatistic {
		Move move;
		int playCount;
		int whiteWins;
		int blackWins;
		
		public MoveStatistic(Move move) {
			this.move = move;
		}
		
		public double getValue() {
			if (playCount == 0) {
				return 0;
			}
			return (double)(whiteWins - blackWins) / playCount;
		}
		
		@Override
		public String toString() {
			return move + " value=" + getValue() + " after " + playCount + " games";
		}
	}

	private Side playGame(Board board, int moveCount) {
		for (int i = 0; i < moveCount; i++) {
			List<Move> allMoves = board.getAllMoves();
			if (allMoves.isEmpty()) {
				return board.getSideToMove().otherSide();
			}
			Move move = randomMove(allMoves);
			board.move(move);
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
		runEngineExample();
	}
	
	public static void runEngineExample() {
		MonteCarloChessEngine chessEngine = new MonteCarloChessEngine();
		
		Board board = new Board();
		//board.setFenString("6k1/rr1q2p1/2bnnpbp/2ppppp1/8/8/PPPPPPPP/RNBQKBNR");
		
		long startMillis = System.currentTimeMillis();

//		double value = chessEngine.evaluatePlaying(board, 1000, 100);
//		System.out.println("VALUE " + value);
		
		Move bestMove = chessEngine.getBestMove(board, 10000);
		System.out.println("BEST " + bestMove);
		
		long endMillis = System.currentTimeMillis();
		System.out.println("TIME " + (endMillis - startMillis) + " ms");

	}
}
