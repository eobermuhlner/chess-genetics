package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MonteCarloChessEngine implements ChessEngine {

	private final Random random = new Random();

	public static class MoveValue {
		public final Move move;
		public final double value;
		
		public MoveValue(Move move, double value) {
			this.move = move;
			this.value = value;
		}
	}

	public static class PositionValue {
		public final Position position;
		public final double value;
		
		public PositionValue(Position position, double value) {
			this.position = position;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("%s(%6.4f)", position, value);
		}
	}

	private InfoLogger infoLogger;

	private Board board;

	private List<Move> allMoves;

	@Override
	public void setInfoLogger(InfoLogger infoLogger) {
		this.infoLogger = infoLogger;
	}
	
	@Override
	public void setStartPosition() {
		board = new Board(infoLogger);
		board.setStartPosition();
	}

	@Override
	public void setFen(String fen) {
		board = new Board(infoLogger);
		board.setFenString(fen);
	}

	@Override
	public double evaluate() {
		return board.getValue();
	}

	@Override
	public String bestMove(long thinkMilliseconds) {
		infoLogger.infoString("position " + board.toFenString());
		infoLogger.infoString("allmoves " + getAllMoves(board));
		
		Move move = getBestMove(board, thinkMilliseconds);
		if (move == null) {
			return "(none)";
		}
		
		return move.getSource().getPositionString() + move.getTargetPositionString();
	}

	@Override
	public void move(String move) {
		board.move(move);
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
		if (thinkMilliseconds == 0) {
			return findBestMoveWithoutThinking(board);
		}
		
		List<Move> allMoves = board.getAllMoves();
		if (allMoves.isEmpty()) {
			return null;
		}

		List<MoveStatistic> moveStatistics = allMoves.stream()
			.map(move -> new MoveStatistic(move))
			.collect(Collectors.toList());
		
		return findBestMove(board, moveStatistics, thinkMilliseconds);
	}

	private Move findBestMoveWithoutThinking(Board board) {
		allMoves = board.getAllMoves();
		if (allMoves.isEmpty()) {
			return null;
		}

		List<MoveValue> allMoveValues = allMoves.stream()
			.map(move -> new MoveValue(move, board.getValue(move)))
			.collect(Collectors.toList());

		return randomMoveValues(allMoveValues);
	}

	public List<PositionValue> getAllPositions(Board board) {
		return board.getPositions().stream()
			.map(position -> new PositionValue(position, board.getValue(position)))
			.sorted((p1, p2) -> {
				return -Double.compare(p1.value, p2.value);
			})
			.collect(Collectors.toList());
	}

	public List<Move> getAllMoves(Board board) {
		List<Move> allMoves = board.getAllMoves();
		allMoves.sort((move1, move2) -> {
			return -Double.compare(move1.getValue(), move2.getValue());
		});
		return allMoves;
	}
	
	public List<MoveValue> getAllMoves(Board board, long thinkMilliseconds, int moveCount) {
		List<Move> allMoves = board.getAllMoves();
		if (allMoves.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<MoveStatistic> moveStatistics = allMoves.stream()
			.map(move -> new MoveStatistic(move))
			.collect(Collectors.toList());

		while (thinkMilliseconds > 0) {
			long startMillis = System.currentTimeMillis();

			moveStatistics.parallelStream().forEach(moveStatistic -> {
				play(board, moveStatistic, moveCount);
			});
			
			long endMillis = System.currentTimeMillis();
			long deltaMillis = endMillis - startMillis;
			thinkMilliseconds -= deltaMillis;
		}
		
		sortStatistics(moveStatistics);
		
		//System.out.println("PLAY COUNT " + moveStatistics.get(0).playCount);
		
		List<MoveValue> result = new ArrayList<>();
		for (MoveStatistic moveStatistic : moveStatistics) {
			result.add(new MoveValue(moveStatistic.move, moveStatistic.getValue()));
		}
		return result;
	}

	private Move findBestMove(Board board, List<MoveStatistic> moveStatistics, long thinkMilliseconds) {
		int moveCount = 10;
		long averagePlayMillis = 10;
		
		long reductionMilliseconds = thinkMilliseconds / 2;
		
		while (thinkMilliseconds > 0) {
			moveStatistics = reduceStatistics(moveStatistics, thinkMilliseconds, reductionMilliseconds, averagePlayMillis);
			
			long thinkStartMillis = System.currentTimeMillis();

			for (MoveStatistic moveStatistic : moveStatistics) {
				play(board, moveStatistic, moveCount);
			}
			
			long thinkEndMillis = System.currentTimeMillis();
			long thinkDeltaMillis = thinkEndMillis - thinkStartMillis;
			thinkMilliseconds -= thinkDeltaMillis;
			
			averagePlayMillis = thinkDeltaMillis / moveStatistics.size();
			//System.out.println("TIME   remaining " + thinkMilliseconds + " ms, thought " + thinkDeltaMillis + " ms, average " + averagePlayMillis + " ms");
		}
		
		sortStatistics(moveStatistics);
		
		System.out.println("STATS " + moveStatistics);
		return pickRandomMove(moveStatistics);
	}

	private Move pickRandomMove(List<MoveStatistic> moveStatistics) {
		double total = 0;
		double min = 0;
		for (MoveStatistic moveStatistic : moveStatistics) {
			double value = moveStatistic.getValue();
			total += value;
			min = Math.min(min, value);
		}
		double offset = -min;
		total += offset * moveStatistics.size();
		
		double r = random.nextDouble() * total;
		
		total = 0;
		for (MoveStatistic moveStatistic : moveStatistics) {
			double value = moveStatistic.getValue();
			total += value + offset;
			if (r < total) {
				return moveStatistic.move;
			}
		}		
		return moveStatistics.get(0).move;
	}

	private void play(Board board, MoveStatistic moveStatistic, int moveCount) {
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

	private List<MoveStatistic> reduceStatistics(List<MoveStatistic> moveStatistics, long thinkMilliseconds, long reductionMilliseconds, long averagePlayMillis) {
		int currentSize = moveStatistics.size();
		int optimumSize;
		if (averagePlayMillis * currentSize * 2 < Math.min(thinkMilliseconds, reductionMilliseconds)) {
			optimumSize = currentSize;
		} else {
			optimumSize = Math.min(currentSize, Math.max(5, currentSize / 2));
		}
		
		if (optimumSize == currentSize) {
			sortStatistics(moveStatistics);
			//System.out.println("KEEP   " + currentSize + " moves : " + moveStatistics.get(0) + " with average " + averagePlayMillis + " ms");
			return moveStatistics;
		} else {
			sortStatistics(moveStatistics);
			//System.out.println("REDUCE " + currentSize + " to " + optimumSize + " moves : " + moveStatistics.get(0) + " with average " + averagePlayMillis + " ms");
			return new ArrayList<>(moveStatistics.subList(0, optimumSize));
		}
	}

	private void sortStatistics(List<MoveStatistic> moveStatistics) {
		moveStatistics.sort((move1, move2) -> {
			int compare = -Double.compare(move1.getValue(), move2.getValue());
			if (compare == 0) {
				compare = -Double.compare(move1.move.getValue(), move2.move.getValue());
			}
			return compare;
		});
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
			return move + " value=" + getValue() + " after " + playCount + " games (" + whiteWins + " white, " + blackWins + " black wins, " + (playCount - whiteWins - blackWins) + " remis)";
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

	private Move randomMoveValues(List<MoveValue> allMoves) {
		if (allMoves.isEmpty()) {
			return null;
		}
		
		double total = 0;
		for (MoveValue moveValue : allMoves) {
			total += moveValue.value;
		}
		
		double r = random.nextDouble() * total;
		
		total = 0;
		for (MoveValue moveValue : allMoves) {
			total += moveValue.value;
			if (r < total) {
				return moveValue.move;
			}
		}

		return allMoves.get(allMoves.size() - 1).move;
	}

	public static void main(String[] args) {
		runEngineExample();
	}
	
	public static void runEngineExample() {
		MonteCarloChessEngine chessEngine = new MonteCarloChessEngine();
		
		Board board = new Board();
		board.setFenString("r1bqkb1r/pppppppp/2n5/5n2/1P4PP/P7/2PPPP1R/RNBQKBN1");

		System.out.println("FEN " + board.toFenString());
		System.out.println("ALLPOSITIONS " + chessEngine.getAllPositions(board));
		System.out.println("ALLMOVES " + chessEngine.getAllMoves(board));
		
		long startMillis = System.currentTimeMillis();

		double value = chessEngine.evaluatePlaying(board, 1000, 100);
		System.out.println("VALUE " + value);
		
		Move bestMove = chessEngine.getBestMove(board, 10000);
		System.out.println("BEST " + bestMove);
		
		long endMillis = System.currentTimeMillis();
		System.out.println("TIME " + (endMillis - startMillis) + " ms");

	}
}
