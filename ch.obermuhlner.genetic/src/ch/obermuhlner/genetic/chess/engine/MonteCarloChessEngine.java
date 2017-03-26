package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MonteCarloChessEngine implements ChessEngine {

	private final Random random = new Random();

	public interface EntityWithValue<E> {
		E getEntity();
		double getValue();
	}
	
	public static class EntityWithFixValue<E> implements EntityWithValue<E> {
		private final E entity;
		private final double value;
		
		public EntityWithFixValue(E entity, double value) {
			this.entity = entity;
			this.value = value;
		}
		
		@Override
		public E getEntity() {
			return entity;
		}
		
		@Override
		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.format("%s(%6.4f)", entity, value);
		}
	}

	private static class MoveStatistic implements EntityWithValue<Move> {
		Move move;
		int playCount;
		int whiteWins;
		int blackWins;
		
		public MoveStatistic(Move move) {
			this.move = move;
		}
		
		@Override
		public Move getEntity() {
			return move;
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

		List<EntityWithValue<Move>> allMoveValues = allMoves.stream()
			.map(move -> new EntityWithFixValue<>(move, board.getValue(move)))
			.collect(Collectors.toList());

		return pickRandom(allMoveValues);
	}

	public List<EntityWithValue<Position>> getAllPositions(Board board) {
		return board.getPositions().stream()
			.map(position -> new EntityWithFixValue<>(position, board.getValue(position)))
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
	
	public List<EntityWithValue<Move>> getAllMoves(Board board, long thinkMilliseconds, int moveCount) {
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
		
		List<EntityWithValue<Move>> result = new ArrayList<>();
		for (MoveStatistic moveStatistic : moveStatistics) {
			result.add(new EntityWithFixValue<Move>(moveStatistic.move, moveStatistic.getValue()));
		}
		return result;
	}

	private Move findBestMove(Board board, List<MoveStatistic> moveStatistics, long thinkMilliseconds) {
		int moveCount = 5;
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
		return pickRandom(moveStatistics);
		//return moveStatistics.get(0).move;
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

	private <MV extends EntityWithValue<Move>> List<MV> reduceStatistics(List<MV> moveStatistics, long thinkMilliseconds, long reductionMilliseconds, long averagePlayMillis) {
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

	private <E> void sortStatistics(List<? extends EntityWithValue<E>> moveStatistics) {
		moveStatistics.sort((move1, move2) -> {
			return -Double.compare(move1.getValue(), move2.getValue());
		});
	}

	private Side playGame(Board board, int moveCount) {
		for (int i = 0; i < moveCount; i++) {
			Move move = findBestMoveWithoutThinking(board);
			if (move == null) {
				return board.getSideToMove().otherSide();
			}
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

	private <E> E pickRandom(List<? extends EntityWithValue<E>> allEntitiesWithValue) {
		if (allEntitiesWithValue.isEmpty()) {
			return null;
		}
		
		// TODO handle offset if negative
		
		double total = 0;
		double min = 0;
		for (EntityWithValue<E> entityWithValue : allEntitiesWithValue) {
			double value = entityWithValue.getValue();
			total += value;
			min = Math.min(min, value);
		}

		double offset = -min;
		total += offset * allEntitiesWithValue.size();

		double r = random.nextDouble() * total;
		
		total = 0;
		for (EntityWithValue<E> entityWithValue : allEntitiesWithValue) {
			total += entityWithValue.getValue() + offset;
			if (r <= total) {
				return entityWithValue.getEntity();
			}
		}

		// should not happen, but just to be save in case of rounding errors
		return allEntitiesWithValue.get(0).getEntity();
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

		System.out.println("VALUE_BOARD " + board.getValue());
		
		double value = chessEngine.evaluatePlaying(board, 1000, 100);
		System.out.println("VALUE_PLAYING " + value);

		System.out.println("BEST_NO_THINKING " + chessEngine.findBestMoveWithoutThinking(board));

		Move bestMove = chessEngine.getBestMove(board, 10000);
		System.out.println("BEST_THINKING " + bestMove);
		
		long endMillis = System.currentTimeMillis();
		System.out.println("TIME " + (endMillis - startMillis) + " ms");

	}
}
