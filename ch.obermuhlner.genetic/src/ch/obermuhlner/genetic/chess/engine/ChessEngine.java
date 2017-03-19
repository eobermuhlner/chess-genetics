package ch.obermuhlner.genetic.chess.engine;

import ch.obermuhlner.genetic.chess.Board;

public class ChessEngine {

	private static final char[] BLACK_FIGURES = {
			Board.BLACK_PAWN,
			Board.BLACK_ROOK,
			Board.BLACK_KNIGHT,
			Board.BLACK_BISHOP,
			Board.BLACK_QUEEN,
			Board.BLACK_KING,
	};
	
	private static final char[] WHITE_FIGURES = {
			Board.WHITE_PAWN,
			Board.WHITE_ROOK,
			Board.WHITE_KNIGHT,
			Board.WHITE_BISHOP,
			Board.WHITE_QUEEN,
			Board.WHITE_KING,
	};
	
	private final Board board;

	public ChessEngine(Board board) {
		this.board = board;
	}
	
	public void randomBlackMove() {
		randomMove(BLACK_FIGURES);
	}

	public void randomWhiteMove() {
		randomMove(WHITE_FIGURES);
	}

	private void randomMove(char[] figures) {
		//findRandomFigure(figures);
	}
	
	private static class FigurePosition {
		public char figure;
		public int x;
		public int y;
	}
}
