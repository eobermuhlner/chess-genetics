package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Board {

	public static class Position {
		public Piece piece;
		public int x;
		public int y;
		public boolean white;

		public Position(Piece piece, int x, int y, boolean white) {
			this.piece = piece;
			this.x = x;
			this.y = y;
			this.white = white;
		}
		
		public double getValue() {
			return piece.getValue();
		}
		
		public char getCharacter() {
			return piece.getCharacter(white);
		}

		@Override
		public String toString() {
			return String.valueOf(getCharacter()) + toPositionString(x, y);
		}
	}
	
	public static class Move {
		Position source;
		int targetX;
		int targetY;
		Position kill;
		Piece convert;
		
		public Move(Position source, int targetX, int targetY, Position kill) {
			this(source, targetX, targetY, kill, null);
		}

		public Move(Position source, int targetX, int targetY, Position kill, Piece convert) {
			this.source = source;
			this.targetX = targetX;
			this.targetY = targetY;
			this.kill = kill;
			this.convert = convert;
		}
		
		public double getValue() {
			if (kill != null) {
				return kill.getValue();
			}
			if (convert != null) {
				return convert.getValue();
			}
			return 0.1;
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append(source);
			result.append(toPositionString(targetX, targetY));
			if (kill != null) {
				result.append("x");
				result.append(kill);
			}
			if (convert != null) {
				result.append(convert.getCharacter(source.white));
			}
			result.append("(");
			result.append(getValue());
			result.append(")");
			
			return result.toString();
		}
	}

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
	
	private final List<Position> positions = new ArrayList<>();
	
	private boolean whiteToMove = true;
	
	public Board() {
		setStartPosition();
	}
	
	public void clear() {
		positions.clear();
	}
	
	public void setStartPosition() {
		clear();
		
		positions.add(new Position(Piece.Rook, 0, 0, true));
		positions.add(new Position(Piece.Knight, 1, 0, true));
		positions.add(new Position(Piece.Bishop, 2, 0, true));
		positions.add(new Position(Piece.Queen, 3, 0, true));
		positions.add(new Position(Piece.King, 4, 0, true));
		positions.add(new Position(Piece.Bishop, 5, 0, true));
		positions.add(new Position(Piece.Knight, 6, 0, true));
		positions.add(new Position(Piece.Rook, 7, 0, true));
		
		positions.add(new Position(Piece.Pawn, 0, 1, true));
		positions.add(new Position(Piece.Pawn, 1, 1, true));
		positions.add(new Position(Piece.Pawn, 2, 1, true));
		positions.add(new Position(Piece.Pawn, 3, 1, true));
		positions.add(new Position(Piece.Pawn, 4, 1, true));
		positions.add(new Position(Piece.Pawn, 5, 1, true));
		positions.add(new Position(Piece.Pawn, 6, 1, true));
		positions.add(new Position(Piece.Pawn, 7, 1, true));
		
		
		positions.add(new Position(Piece.Rook, 0, 7, false));
		positions.add(new Position(Piece.Knight, 1, 7, false));
		positions.add(new Position(Piece.Bishop, 2, 7, false));
		positions.add(new Position(Piece.Queen, 3, 7, false));
		positions.add(new Position(Piece.King, 4, 7, false));
		positions.add(new Position(Piece.Bishop, 5, 7, false));
		positions.add(new Position(Piece.Knight, 6, 7, false));
		positions.add(new Position(Piece.Rook, 7, 7, false));
		
		positions.add(new Position(Piece.Pawn, 0, 6, false));
		positions.add(new Position(Piece.Pawn, 1, 6, false));
		positions.add(new Position(Piece.Pawn, 2, 6, false));
		positions.add(new Position(Piece.Pawn, 3, 6, false));
		positions.add(new Position(Piece.Pawn, 4, 6, false));
		positions.add(new Position(Piece.Pawn, 5, 6, false));
		positions.add(new Position(Piece.Pawn, 6, 6, false));
		positions.add(new Position(Piece.Pawn, 7, 6, false));
		
		analyzePosition();
	}

	public void setFenString(String fen) {
		List<Position> fenPositions = toFenPositions(fen);
		
		clear();
		positions.addAll(fenPositions);
		analyzePosition();
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
				return new Position(piece, x, y, true);
			}
			if (piece.getBlackCharacter() == character) {
				return new Position(piece, x, y, false);
			}
		}
		return null;
	}
	
	public void addPosition(Piece piece, int x, int y, boolean white) {
		positions.add(new Position(piece, x, y, white));
		analyzePosition();
	}
	
	private void analyzePosition() {
		
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
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
		return positions.stream()
			.filter(position -> position.x == x && position.y == y)
			.findFirst().orElse(null);
	}
	
	public double getSideValue(boolean white) {
		double value = positions.stream()
				.filter(position -> position.white == white)
				.mapToDouble(position -> position.getValue())
				.sum();
		
		if (whiteToMove == white) {
			value += 0.5;
		}
		
		return value;
	}
	
	public double getValue() {
		return getSideValue(true) -getSideValue(false);
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
			.filter(position -> position.white == whiteToMove)
			.filter(position -> position.piece == Piece.King)
			.flatMap(position -> getAllMoves(position).stream())
			.collect(Collectors.toList()));
		
		// TODO find moves that kill checking piece
		// TODO find moves that intercept checking move
		
		return moves;
	}

	private List<Move> getAllMovesNormal() {
		List<Move> moves = new ArrayList<>();

		moves.addAll(positions.stream()
				.filter(position -> position.white == whiteToMove)
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
		List<Move> moves = new ArrayList<>();

		switch(position.piece) {
		case Pawn:
			addPawnMoves(position, moves);
			break;
		case Knight:
			addKnightMoves(position, moves);
			break;
		case Bishop:
			addBishopMoves(position, moves);
			break;
		case Rook:
			addRookMoves(position, moves);
			break;
		case Queen:
			addQueenMoves(position, moves);
			break;
		case King:
			addKingMoves(position, moves);
			break;
		}

		return moves;
	}

	private void addPawnMoves(Position position, List<Move> moves) {
		int direction = position.white ? 1 : -1;
		
		if (addMovePawnIfFree(position, position.x, position.y + direction, moves) && isPawnStart(position)) {
			addMovePawnIfFree(position, position.x, position.y + direction + direction, moves);
		}
		addMoveMustKill(position, position.x + 1, position.y + direction, moves);
		addMoveMustKill(position, position.x - 1, position.y + direction, moves);
		
		// TODO add en-passant
	}
	
	private void addKnightMoves(Position position, List<Move> moves) {
		addMove(position, position.x-2, position.y+1, moves);
		addMove(position, position.x-1, position.y+2, moves);
		addMove(position, position.x+1, position.y+2, moves);
		addMove(position, position.x+2, position.y+1, moves);
		addMove(position, position.x+2, position.y-1, moves);
		addMove(position, position.x+1, position.y-2, moves);
		addMove(position, position.x-1, position.y-2, moves);
		addMove(position, position.x-2, position.y-1, moves);
	}

	private void addRookMoves(Position position, List<Move> moves) {
		addRayMoves(position, -1, 0, moves);
		addRayMoves(position, +1, 0, moves);
		addRayMoves(position, 0, -1, moves);
		addRayMoves(position, 0, +1, moves);
	}

	private void addBishopMoves(Position position, List<Move> moves) {
		addRayMoves(position, -1, -1, moves);
		addRayMoves(position, +1, -1, moves);
		addRayMoves(position, -1, +1, moves);
		addRayMoves(position, +1, +1, moves);
	}

	private void addQueenMoves(Position position, List<Move> moves) {
		addRayMoves(position, -1, 0, moves);
		addRayMoves(position, +1, 0, moves);
		addRayMoves(position, 0, -1, moves);
		addRayMoves(position, 0, +1, moves);
		
		addRayMoves(position, -1, -1, moves);
		addRayMoves(position, +1, -1, moves);
		addRayMoves(position, -1, +1, moves);
		addRayMoves(position, +1, +1, moves);
	}

	private void addRayMoves(Position position, int directionX, int directionY, List<Move> moves) {
		int x = position.x;
		int y = position.y;
		
		do {
			x += directionX;
			y += directionY;
		} while(addMove(position, x, y, moves));
	}
	
	private void addKingMoves(Position position, List<Move> moves) {
		addMoveIfSave(position, position.x-1, position.y-1, moves);
		addMoveIfSave(position, position.x-1, position.y+0, moves);
		addMoveIfSave(position, position.x-1, position.y+1, moves);
		addMoveIfSave(position, position.x+0, position.y-1, moves);
		addMoveIfSave(position, position.x+0, position.y+0, moves);
		addMoveIfSave(position, position.x+0, position.y+1, moves);
		addMoveIfSave(position, position.x+1, position.y-1, moves);
		addMoveIfSave(position, position.x+1, position.y+0, moves);
		addMoveIfSave(position, position.x+1, position.y-1, moves);
	}
	
	private boolean isPawnStart(Position position) {
		if (position.white) {
			return position.y == 1;
		} else {
			return position.y == 6;
		}
	}

	private boolean addMoveIfSave(Position position, int targetX, int targetY, List<Move> moves) {
		// TODO verify if safe from attack
		return addMove(position, targetX, targetY, moves);
	}
	
	private boolean addMovePawnIfFree(Position position, int targetX, int targetY, List<Move> moves) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target == null) {
			if (targetY == getLastRow(position.white)) {
				for (Piece convert : Arrays.asList(Piece.Knight, Piece.Bishop, Piece.Rook, Piece.Queen)) {
					moves.add(new Move(position, targetX, targetY, target, convert));
				}
			} else {
				moves.add(new Move(position, targetX, targetY, target));
			}
			return true;
		}
		return false;
	}

	private static int getLastRow(boolean white) {
		if (white) {
			return 7;
		} else { 
			return 0;
		}
	}

	private boolean addMoveMustKill(Position position, int targetX, int targetY, List<Move> moves) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target != null && target.white != position.white) {
			moves.add(new Move(position, targetX, targetY, target));
			return true;
		}
		return false;
	}

	private boolean addMove(Position position, int targetX, int targetY, List<Move> moves) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target == null || target.white != position.white) {
			moves.add(new Move(position, targetX, targetY, target));
			return true;
		}
		return false;
	}

	public void move(Move move) {
		positions.remove(move.source);
		positions.remove(move.kill);
		
		Piece piece = move.convert == null ? move.source.piece : move.convert;
		Position newPosition = new Position(piece, move.targetX, move.targetY, move.source.white);
		
		positions.add(newPosition);
		whiteToMove = !whiteToMove;
		
		analyzePosition();
	}
	
	public Board clone() {
		Board board = new Board();
		
		board.positions.addAll(positions);
		board.whiteToMove = whiteToMove;
		
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
			charBoard[position.x + (7-position.y) * 8] = position.getCharacter();
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
