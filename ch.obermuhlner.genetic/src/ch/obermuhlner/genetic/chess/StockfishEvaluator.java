package ch.obermuhlner.genetic.chess;

import ch.obermuhlner.genetic.GenomeEvaluator;

public class StockfishEvaluator implements GenomeEvaluator<StartPosition> {

	private final String chessEngine = "C:/Apps/stockfish-8-win/Windows/stockfish_8_x64";
	
	@Override
	public double evaluate(StartPosition first, StartPosition second) {

		Board board = StartPosition.toBoard(first, second);
		System.out.println(board);

		return 0;
	}
}
