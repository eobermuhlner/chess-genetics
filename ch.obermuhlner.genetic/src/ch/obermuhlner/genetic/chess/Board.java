package ch.obermuhlner.genetic.chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Board {

	public static final char EMPTY = ' ';

	public static final char BLACK_KING = 'k';
	public static final char BLACK_QUEEN = 'q';
	public static final char BLACK_BISHOP = 'b';
	public static final char BLACK_KNIGHT = 'n';
	public static final char BLACK_ROOK = 'r';
	public static final char BLACK_PAWN = 'p';

	public static final char WHITE_KING = 'K';
	public static final char WHITE_QUEEN = 'Q';
	public static final char WHITE_BISHOP = 'B';
	public static final char WHITE_KNIGHT = 'N';
	public static final char WHITE_ROOK = 'R';
	public static final char WHITE_PAWN = 'P';

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
	
	public static final Set<Character> BLACK_FIGURES = new HashSet<>(Arrays.asList(BLACK_KING, BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT, BLACK_PAWN));
	public static final Set<Character> WHITE_FIGURES = new HashSet<>(Arrays.asList(WHITE_KING, WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT, WHITE_PAWN));
	public static final Set<Character> ALL_FIGURES = new HashSet<>();
	
	static {
		ALL_FIGURES.addAll(BLACK_FIGURES);
		ALL_FIGURES.addAll(WHITE_FIGURES);
	}

	private int width;
	private int height;

	private final char fields[];

	public Board() {
		this(8, 8);
	}
	
	public Board(String fen) {
		this(8, 8);
		
		setFen(fen);
	}
	
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
	
		fields = new char[width * height];
		
		for (int i = 0; i < fields.length; i++) {
			fields[i] = EMPTY;
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public char getField(int x, int y) {
		return getField(x + y * width);
	}

	public char getField(int index) {
		return fields[index];
	}

	public void setField(int x, int y, char figure) {
		setField(x + y * width, figure);
	}

	public void setField(int index, char figure) {
		fields[index] = figure;
	}
	
	public void clear() {
		for (int i = 0; i < fields.length; i++) {
			fields[i] = EMPTY;
		}
	}
	
	public void setFen(String fen) {
		clear();
		
		int index = 0;
		for (int i = 0; i < fen.length(); i++) {
			char c = fen.charAt(i);
			if (ALL_FIGURES.contains(c)) {
				fields[index] = c;
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

	@Override
	public String toString() {
		return toFenString();
	}

	public String toSimpleString() {
		StringBuilder builder = new StringBuilder();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				builder.append(getField(x, y));
			}
			builder.append("/");
		}
		
		return builder.toString();
	}

	public String toFenString() {
		StringBuilder builder = new StringBuilder();
		
		for (int y = 0; y < height; y++) {
			int emptyCount = 0;
			for (int x = 0; x < width; x++) {
				char figure = getField(x, y);
				if (figure == Board.EMPTY) {
					emptyCount++;
				} else {
					if (emptyCount > 0) {
						builder.append(emptyCount);
						emptyCount = 0;
					}
					builder.append(figure);
				}
			}

			if (emptyCount > 0) {
				builder.append(emptyCount);
			}
			
			if (y != height - 1) {
				builder.append("/");
			}
		}
		
		return builder.toString();
	}
	
	public String toViewerUrl() {
		return "https://lichess.org/editor/" + toFenString();
	}

	public static char toWhite(char figure) {
		return Character.toUpperCase(figure);
	}
	
	public static char toBlack(char figure) {
		return Character.toLowerCase(figure);
	}

	public static int value(char figure) {
		return toValue(toBlack(figure));
	}

	private static int toValue(char blackFigure) {
		switch(blackFigure) {
		case BLACK_PAWN:
			return 1;
		case BLACK_KNIGHT:
			return 2;
		case BLACK_BISHOP:
			return 3;
		case BLACK_ROOK:
			return 4;
		case BLACK_QUEEN:
			return 5;
		case BLACK_KING:
			return 6;
		}
		
		return 0;
	}
}
