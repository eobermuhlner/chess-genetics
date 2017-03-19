package ch.obermuhlner.genetic.chess;

import java.util.Random;

public class StartPosition extends Board {

	public StartPosition(String fen) {
		this();
		
		setFen(fen);
	}

	public StartPosition() {
		super(8, 4);
	}

	public StartPosition copy() {
		StartPosition result = new StartPosition();
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				result.setField(x, y, getField(x, y));
			}
		}
		
		return result;
	}
	
	public int randomX(Random random) {
		return random.nextInt(getWidth());
	}

	public int randomY(Random random) {
		return random.nextInt(getHeight());
	}

	public int randomY(Random random, char figure) {
//		if (figure == Board.BLACK_PAWN || figure == Board.WHITE_PAWN) {
//			return random.nextInt(getHeight() - 1) + 1;
//		}
		return random.nextInt(getHeight());
	}

	public static Board toBoard(StartPosition white, StartPosition black) {
		Board board = new Board(Math.max(white.getWidth(), black.getWidth()), white.getHeight() + black.getHeight());
		
		for (int y = 0; y < black.getHeight(); y++) {
			for (int x = 0; x < black.getWidth(); x++) {
				board.setField(x, y, Board.toBlack(black.getField(x, y)));
			}
		}

		for (int y = 0; y < white.getHeight(); y++) {
			for (int x = 0; x < white.getWidth(); x++) {
				board.setField(x, board.getHeight() - y - 1, Board.toWhite(white.getField(x, y)));
			}
		}
		
		return board;
	}

}
