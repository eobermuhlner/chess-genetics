package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.List;

public class ChessEngine {

	private Board board;

	public ChessEngine(Board board) {
		this.board = board;
	}
	
	public List<Move> possibleMoves() {
		List<Move> result = new ArrayList<>();
		
		return result;
	}
	
	public static class Move {
		public char figure;
		public long from;
		public long to;
		public char takeFigure;
	}
}
