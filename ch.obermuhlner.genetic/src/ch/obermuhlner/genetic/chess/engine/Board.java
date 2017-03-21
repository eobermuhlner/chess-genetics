package ch.obermuhlner.genetic.chess.engine;

import java.util.HashSet;
import java.util.Set;

public class Board {
	public enum Figure {
		WhitePawn('P', 1),
		WhiteKnight('N', 3),
		WhiteBishop('B', 3),
		WhiteRook('R', 4),
		WhiteQueen('Q', 5),
		WhiteKing('K', 6),
		BlackPawn('p', 1),
		BlackKnight('n', 3),
		BlackBishop('b', 3),
		BlackRook('r', 4),
		BlackQueen('q', 5),
		BlackKing('k', 6);
		
		private char character;
		private double baseValue;

		Figure(char character, double baseValue) {
			this.character = character;
			this.baseValue = baseValue;
		}
		
		public char getCharacter() {
			return character;
		}
	}
	
	public static final char BLACK_PAWN = 'p';
	public static final char BLACK_KNIGHT = 'n';
	public static final char BLACK_BISHOP = 'b';
	public static final char BLACK_ROOK = 'r';
	public static final char BLACK_QUEEN = 'q';
	public static final char BLACK_KING = 'k';

	public static final char WHITE_PAWN = 'P';
	public static final char WHITE_KNIGHT = 'N';
	public static final char WHITE_BISHOP = 'B';
	public static final char WHITE_ROOK = 'R';
	public static final char WHITE_QUEEN = 'Q';
	public static final char WHITE_KING = 'K';
	
	private static final int PAWN_INDEX = 0;
	private static final int KNIGHT_INDEX = 1;
	private static final int BISHOP_INDEX = 2;
	private static final int ROOK_INDEX = 3;
	private static final int QUEEN_INDEX = 4;
	private static final int KING_INDEX = 5;

	private static final int START_INDEX = 0;
	private static final int WHITE_START_INDEX = START_INDEX;
	private static final int WHITE_END_INDEX = WHITE_START_INDEX + KING_INDEX + 1;
	private static final int BLACK_START_INDEX = WHITE_END_INDEX;
	private static final int BLACK_END_INDEX = BLACK_START_INDEX + KING_INDEX + 1;
	private static final int END_INDEX = BLACK_START_INDEX + KING_INDEX + 1;

	
	public static final char[] BLACK_INITIAL_POSITION = {
			Board.BLACK_ROOK,
			Board.BLACK_KNIGHT,
			Board.BLACK_BISHOP,
			Board.BLACK_QUEEN,
			Board.BLACK_KING,
			Board.BLACK_BISHOP,
			Board.BLACK_KNIGHT,
			Board.BLACK_ROOK,

			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
			Board.BLACK_PAWN,
	};
	
	private static final char[] WHITE_FIGURES = { WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING};
	private static final char[] BLACK_FIGURES = { BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING};
	private static final char[] ALL_FIGURES = {
			WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING,
			BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING};
	private static final Set<Character> ALL_FIGURES_SET = new HashSet<>();
	static {
		for (int figureIndex = 0; figureIndex <= KING_INDEX; figureIndex++) {
			ALL_FIGURES_SET.add(WHITE_FIGURES[figureIndex]);
			ALL_FIGURES_SET.add(BLACK_FIGURES[figureIndex]);
		}
	}

	private long[] figures = new long[END_INDEX];
	
	public boolean whiteToPlay;
	
	public void clear() {
		for (int i = 0; i < END_INDEX; i++) {
			figures[i] = 0;
		}
	}
	
	public void setFen(String fen) {
		clear();
		
		int index = 0;
		for (int i = 0; i < fen.length(); i++) {
			char c = fen.charAt(i);
			if (ALL_FIGURES_SET.contains(c)) {
				addFigure(index, c);
				index++;
			} else if (c >= '1' && c <= '9') {
				int emptyCount = Character.getNumericValue(c);
				index += emptyCount;
			} else if (c == '/') {
				// ignore
			} else if (c == ' ') {
				return;
			}
		}
	}

	private void addFigure(int index, char c) {
		long position = indexToLong(index);
		switch (c) {
		case WHITE_PAWN:
			figures[WHITE_START_INDEX + PAWN_INDEX] |= position;
			break;
		case WHITE_BISHOP:
			figures[WHITE_START_INDEX + BISHOP_INDEX] |= position;
			break;
		case WHITE_KNIGHT:
			figures[WHITE_START_INDEX + KNIGHT_INDEX] |= position;
			break;
		case WHITE_ROOK:
			figures[WHITE_START_INDEX + ROOK_INDEX] |= position;
			break;
		case WHITE_QUEEN:
			figures[WHITE_START_INDEX + QUEEN_INDEX] |= position;
			break;
		case WHITE_KING:
			figures[WHITE_START_INDEX + KING_INDEX] |= position;
			break;
		case BLACK_PAWN:
			figures[BLACK_START_INDEX + PAWN_INDEX] |= position;
			break;
		case BLACK_BISHOP:
			figures[BLACK_START_INDEX + BISHOP_INDEX] |= position;
			break;
		case BLACK_KNIGHT:
			figures[BLACK_START_INDEX + KNIGHT_INDEX] |= position;
			break;
		case BLACK_ROOK:
			figures[BLACK_START_INDEX + ROOK_INDEX] |= position;
			break;
		case BLACK_QUEEN:
			figures[BLACK_START_INDEX + QUEEN_INDEX] |= position;
			break;
		case BLACK_KING:
			figures[BLACK_START_INDEX + KING_INDEX] |= position;
			break;
		}
	}

	@Override
	public String toString() {
		return toFenString();
	}
	
	private String toDebugString() {
		StringBuilder result = new StringBuilder();
		
		for (int i = START_INDEX; i < END_INDEX; i++) {
			result.append(ALL_FIGURES[i]);
			result.append(" : ");
			result.append(String.format("%016x", figures[i]));
			result.append("\n");
		}
		
		return result.toString();
	}

	public String toFenString() {
		StringBuilder result = new StringBuilder();
		
		long allFigures = ArrayUtil.or(figures);

		int emptyCount = 0;
		for (int i = 0; i < 64; i++) {
			long position = indexToLong(i);
			if ((allFigures & position) == 0) {
				emptyCount++;
			} else {
				if (emptyCount > 0) {
					result.append(emptyCount);
					emptyCount = 0;
				}
				for (int figureIndex = START_INDEX; figureIndex < END_INDEX; figureIndex++) {
					if ((figures[figureIndex] & position) != 0) {
						result.append(ALL_FIGURES[figureIndex]);
					}
				}
			}
			
			if ((i % 8) == 7) {
				if (emptyCount > 0) {
					result.append(emptyCount);
					emptyCount = 0;
				}
				if (i != 63) {
					result.append("/");
				}
			}
		}
		
		return result.toString();
	}

	private long indexToLong(int index) {
		return 1L << index;
	}
	
}
