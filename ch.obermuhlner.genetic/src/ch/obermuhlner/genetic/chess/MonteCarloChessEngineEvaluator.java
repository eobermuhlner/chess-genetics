package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.GenomeEvaluator;
import ch.obermuhlner.genetic.chess.engine.MonteCarloChessEngine;

public class MonteCarloChessEngineEvaluator implements GenomeEvaluator<StartPosition> {

	private final int gameCount;
	private final int moveCount;
	private final boolean evaluatePlaying;
	
	private final MonteCarloChessEngine chessEngine = new MonteCarloChessEngine();

	public MonteCarloChessEngineEvaluator() {
		this(100, 100, true);
	}
	
	public MonteCarloChessEngineEvaluator(int gameCount, int moveCount, boolean evaluatePlaying) {
		this.gameCount = gameCount;
		this.moveCount = moveCount;
		this.evaluatePlaying = evaluatePlaying;
	}

	@Override
	public double evaluate(StartPosition first, StartPosition second) {
		double valueFirstAsWhite = evaluatePlay(first, second);
		double valueFirstAsBlack = evaluatePlay(second, first);
		return valueFirstAsWhite - valueFirstAsBlack;
	}
	
	private double evaluatePlay(StartPosition white, StartPosition black) {
		String fenString = StartPosition.toBoard(white, black).toFenString();
		
		ch.obermuhlner.genetic.chess.engine.Board board = new ch.obermuhlner.genetic.chess.engine.Board();
		board.setFenString(fenString);

		if (evaluatePlaying) {
			return chessEngine.evaluatePlaying(board, gameCount, moveCount);
		} else {
			return chessEngine.evaluatePosition(board);
		}
	}

}
