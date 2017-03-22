package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Board {

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
	
	final List<Position> positions = new ArrayList<>();
	
	private Side sideToMove = Side.White;
	
	private Analysis analysis;
	
	public Board() {
		setStartPosition();
	}
	
	public void clear() {
		positions.clear();
	}
	
	public void setStartPosition() {
		clear();
		
		positions.add(new Position(Piece.Rook, Side.White, 0, 0));
		positions.add(new Position(Piece.Knight, Side.White, 1, 0));
		positions.add(new Position(Piece.Bishop, Side.White, 2, 0));
		positions.add(new Position(Piece.Queen, Side.White, 3, 0));
		positions.add(new Position(Piece.King, Side.White, 4, 0));
		positions.add(new Position(Piece.Bishop, Side.White, 5, 0));
		positions.add(new Position(Piece.Knight, Side.White, 6, 0));
		positions.add(new Position(Piece.Rook, Side.White, 7, 0));
		
		positions.add(new Position(Piece.Pawn, Side.White, 0, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 1, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 2, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 3, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 4, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 5, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 6, 1));
		positions.add(new Position(Piece.Pawn, Side.White, 7, 1));
		
		
		positions.add(new Position(Piece.Rook, Side.Black, 0, 7));
		positions.add(new Position(Piece.Knight, Side.Black, 1, 7));
		positions.add(new Position(Piece.Bishop, Side.Black, 2, 7));
		positions.add(new Position(Piece.Queen, Side.Black, 3, 7));
		positions.add(new Position(Piece.King, Side.Black, 4, 7));
		positions.add(new Position(Piece.Bishop, Side.Black, 5, 7));
		positions.add(new Position(Piece.Knight, Side.Black, 6, 7));
		positions.add(new Position(Piece.Rook, Side.Black, 7, 7));
		
		positions.add(new Position(Piece.Pawn, Side.Black, 0, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 1, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 2, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 3, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 4, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 5, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 6, 6));
		positions.add(new Position(Piece.Pawn, Side.Black, 7, 6));
		
		invalidateAnalysis();
	}

	public void setFenString(String fen) {
		List<Position> fenPositions = toFenPositions(fen);
		
		clear();
		positions.addAll(fenPositions);
		invalidateAnalysis();
	}

	private static List<Position> toFenPositions(String fen) {
		List<Position> fenPositions = new ArrayList<>();
		
		int index = 0;
		for (int i = 0; i < fen.length(); i++) {
			char c = fen.charAt(i);
			Position position = toPosition(c, index);
			if (position != null) {
				fenPositions.add(position);
				index++;
			} else if (c >= '1' && c <= '9') {
				int emptyCount = Character.getNumericValue(c);
				index += emptyCount;
			} else if (c == '/') {
				// ignore
			} else if (c == ' ') {
				return fenPositions;
			} else {
				throw new IllegalArgumentException("Unknown character '" + c + "' in FEN string: " + fen);
			}
		}
		return fenPositions;
	}
	
	private static Position toPosition(char character, int index) {
		int x = index % 8;
		int y = 7 - index / 8;
	
		for (Piece piece : Piece.values()) {
			if (piece.getWhiteCharacter() == character) {
				return new Position(piece, Side.White, x, y);
			}
			if (piece.getBlackCharacter() == character) {
				return new Position(piece, Side.Black, x, y);
			}
		}
		return null;
	}

	public void addPosition(String position) {
		char character = position.charAt(0);
		
		for (Piece piece : Piece.values()) {
			if (piece.getWhiteCharacter() == character) {
				addPosition(piece, Side.White, position.substring(1));
				return;
			}
			if (piece.getBlackCharacter() == character) {
				addPosition(piece, Side.Black, position.substring(1));
				return;
			}
		}
		
		throw new IllegalArgumentException("Unknown position: " + position);
	}
	
	public void addPosition(Piece piece, Side side, String position) {
		int x = letterToInt(position.charAt(0));
		int y = Character.getNumericValue(position.charAt(1)) - 1;
		addPosition(piece, side, x, y);
	}
	
	public void setSideToMove(Side sideToMove) {
		this.sideToMove = sideToMove;
		invalidateAnalysis();
	}
	
	private static int letterToInt(char letter) {
		for (int i = 0; i < LETTERS.length; i++) {
			if (letter == LETTERS[i]) {
				return i;
			}
		}
		
		throw new IllegalArgumentException("Unknown chess position letter: " + letter);
	}
	
	public void addPosition(Piece piece, Side side, int x, int y) {
		positions.add(new Position(piece, side, x, y));
		invalidateAnalysis();
	}
	
	private void invalidateAnalysis() {
		analysis = null;		
	}

	private Analysis getAnalysis() {
		if (analysis == null) {
			analysis = new Analysis(this);
		}
		
		return analysis;
	}
		
	public Side getSideToMove() {
		return sideToMove;
	}
	
	public boolean isMate() {
		// TODO
		return false;
	}
	
	public boolean isPatt() {
		// TODO
		return false;
	}
	
	public boolean isCheck() {
		// TODO
		return false;
	}
	
	public Position getPosition(int x, int y) {
		return getAnalysis().getPosition(x, y);
	}
	
	public double getValue() {
		return getSideValue(Side.White) - getSideValue(Side.Black);
	}
	
	public double getSideValue(Side side) {
		double value = positions.stream()
				.filter(position -> position.getSide() == side)
				.mapToDouble(position -> getValue(position))
				.sum();
		
		if (sideToMove == side) {
			value += 0.5;
		}
		
		return value;
	}
	
	private double getValue(Position position) {
		double value = position.getPiece().getValue();
		
		switch(position.getPiece()) {
		case Pawn:
			value *= 0.9 + getPawnLine(position) * 0.2;
			break;
		case King:
			break;
		case Knight:
		case Bishop:
		case Rook:
		case Queen:
			value *= 0.9 + getMobilityFactor(position) * 0.1;
			break;
		}
		
		value *= 0.9 + getAttacksFactor(position) * 0.2;
		value *= 0.9 + getDefendsFactor(position) * 0.15;
		
		return value;
	}
	
	private double getMobilityFactor(Position position) {
		return (double) getAllMoves(position).size() / position.getPiece().getMaxMoves();
	}

	private double getAttacksFactor(Position position) {
		return (double) getAllAttacks(position).size() / position.getPiece().getMaxAttacks();
	}

	private double getDefendsFactor(Position position) {
		return (double) getAllDefends(position).size() / position.getPiece().getMaxAttacks();
	}

	public List<Move> getAllMoves() {
		if (isMate() || isPatt()) {
			return Collections.emptyList();
		}
		
		if (isCheck()) {
			return getAllMovesUnderCheck();
		}
		
		return getAllMovesNormal();
	}
	
	private List<Move> getAllMovesUnderCheck() {
		List<Move> moves = new ArrayList<>();
		
		moves.addAll(positions.stream()
			.filter(position -> position.getSide() == sideToMove)
			.filter(position -> position.getPiece() == Piece.King)
			.flatMap(position -> getAllMoves(position).stream())
			.collect(Collectors.toList()));
		
		// TODO find moves that kill checking piece
		// TODO find moves that intercept checking move
		
		return moves;
	}

	private List<Move> getAllMovesNormal() {
		List<Move> moves = new ArrayList<>();

		moves.addAll(positions.stream()
				.filter(position -> position.getSide() == sideToMove)
				.filter(position -> !moveWillLeaveInCheck(position))
				.flatMap(position -> getAllMoves(position).stream())
				.collect(Collectors.toList()));

		return moves;
	}
	
	private boolean moveWillLeaveInCheck(Position position) {
		// TODO Auto-generated method stub
		return false;
	}

	private List<Move> getAllMoves(Position position) {
		return getAnalysis().getPositionMovesMap().get(position);
	}

	private List<Position> getAllAttacks(Position position) {
		return getAnalysis().getPositionAttacksMap().get(position);
	}

	private List<Position> getAllDefends(Position position) {
		return getAnalysis().getPositionDefendsMap().get(position);
	}

	private static int getPawnLine(Position position) {
		switch(position.getSide()) {
		case White:
			return position.getY();
		case Black:
			return 7 - position.getY();
		}
		throw new IllegalArgumentException("Unknown side: " + position.getSide());
	}

	static int getPawnDirection(Side side) {
		switch(side) {
		case White:
			return 1;
		case Black:
			return -1;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	static int getPawnStart(Side side) {
		switch(side) {
		case White:
			return 1;
		case Black:
			return 6;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	static int getLastRow(Side side) {
		switch(side) {
		case White:
			return 7;
		case Black:
			return 0;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	public void move(Move move) {
		positions.remove(move.getSource());
		positions.remove(move.getKill());
		
		Piece piece = move.getConvert() == null ? move.getSource().getPiece() : move.getConvert();
		Position newPosition = new Position(piece, move.getSource().getSide(), move.getTargetX(), move.getTargetY());
		
		positions.add(newPosition);
		sideToMove = sideToMove.otherSide();
		
		invalidateAnalysis();
	}
	
	public Board clone() {
		Board board = new Board();
		
		board.positions.addAll(positions);
		board.sideToMove = sideToMove;
		
		return board;
	}
	
	@Override
	public String toString() {
		return toFenString();
	}

	public String toFenString() {
		StringBuilder builder = new StringBuilder();

		char[] charBoard = new char[64];
		for (int i = 0; i < charBoard.length; i++) {
			charBoard[i] = ' ';
		}
		
		for(Position position : positions) {
			charBoard[position.getX() + (7-position.getY()) * 8] = position.getCharacter();
		}
		
		for (int y = 0; y < 8; y++) {
			int emptyCount = 0;
			for (int x = 0; x < 8; x++) {
				char figure = charBoard[x + y * 8];
				if (figure == ' ') {
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
			
			if (y != 7) {
				builder.append("/");
			}
		}
		
		return builder.toString();
	}

	public static String toPositionString(int x, int y) {
		return String.valueOf(LETTERS[x]) + (y + 1);
	}
	
	public static void main(String[] args) {
		Random random = new Random(1234);
		
		Board board = new Board();
		board.setStartPosition();

		for (int i = 0; i < 100; i++) {
			System.out.println("STEP " + i);
			System.out.println(board.toFenString());
			List<Move> allMoves = board.getAllMoves();
			System.out.println("VALUE " + board.getValue());
			System.out.println("ALL   " + allMoves);
			
			if (!allMoves.isEmpty()) {
				Move move = randomMove(allMoves, random);
				System.out.println("MOVE  " + move);
				board.move(move);
			}
			System.out.println();
		}
	}
	
	private static Move randomMove(List<Move> allMoves, Random random) {
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
}
