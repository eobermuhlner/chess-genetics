package ch.obermuhlner.genetic.chess;

import java.util.Random;

import ch.obermuhlner.genetic.GenomeFactory;

public class StartPositionFactory implements GenomeFactory<StartPosition> {

	private static final char[] FIGURES = {
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			
			Board.BLACK_ROOK,
			Board.BLACK_ROOK,
			Board.BLACK_KNIGHT,
			Board.BLACK_KNIGHT,
			Board.BLACK_BISHOP,
			Board.BLACK_BISHOP,

			Board.BLACK_KING,
			Board.BLACK_QUEEN,
	};

	private final Random random = new Random();
	
	@Override
	public StartPosition createGenom() {
		StartPosition startPosition = new StartPosition();

		for (int index = 0; index < FIGURES.length; index++) {
			char figure = FIGURES[index];

			int x;
			int y;
			do {
				x = startPosition.randomX(random);
				y = startPosition.randomY(random, figure);
			} while (startPosition.getField(x, y) != Board.EMPTY);
			startPosition.setField(x, y, figure);
		}
		
		return startPosition;
	}

}
