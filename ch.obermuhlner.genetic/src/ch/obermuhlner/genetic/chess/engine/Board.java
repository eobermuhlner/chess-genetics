package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Board {

	public static class Position {
		public Piece piece;
		public int x;
		public int y;
		public Side side;

		public Position(Piece piece, Side side, int x, int y) {
			this.piece = piece;
			this.x = x;
			this.y = y;
			this.side = side;
		}
		
		public char getCharacter() {
			return piece.getCharacter(side);
		}
		
		public String getPositionString() {
			return toPositionString(x, y);
		}

		@Override
		public String toString() {
			return String.valueOf(getCharacter()) + getPositionString();
		}
	}
	
	public static class Move {
		private Position source;
		private int targetX;
		private int targetY;
		private Position kill;
		private Piece convert;
		
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
		
		public Position getSource() {
			return source;
		}
		
		public String getTargetPositionString() {
			return toPositionString(targetX, targetY);
		}
		
		public double getValue() {
			if (kill != null) {
				return kill.piece.getValue();
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
				result.append("=");
				result.append(convert.getCharacter(source.side));
			}
			result.append("(");
			result.append(getValue());
			result.append(")");
			
			return result.toString();
		}
	}
	
	private static class Analysis {
		private Map<Position, List<Move>> positionMovesMap = new HashMap<>();
		private Map<Position, List<Position>> positionAttacksMap = new HashMap<>();
		private Map<Position, List<Position>> positionDefendsMap = new HashMap<>();
	}
	
	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
	
	private final List<Position> positions = new ArrayList<>();
	
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
			analysis = createAnalysis();
		}
		
		return analysis;
	}
		
	private Analysis createAnalysis() {
		Analysis analysis = new Analysis();
		analysis.positionMovesMap.clear();
		analysis.positionAttacksMap.clear();
		analysis.positionDefendsMap.clear();
		
		for (Position position : positions) {
			List<Move> moves = new ArrayList<>();
			List<Position> attacks = new ArrayList<>();
			List<Position> defends = new ArrayList<>();
			
			addAllMoves(position, moves, attacks, defends);
			
			analysis.positionMovesMap.put(position, moves);
			analysis.positionAttacksMap.put(position, attacks);
			analysis.positionDefendsMap.put(position, defends);
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
		return positions.stream()
			.filter(position -> position.x == x && position.y == y)
			.findFirst().orElse(null);
	}
	
	public double getValue() {
		return getSideValue(Side.White) - getSideValue(Side.Black);
	}
	
	public double getSideValue(Side side) {
		double value = positions.stream()
				.filter(position -> position.side == side)
				.mapToDouble(position -> getValue(position))
				.sum();
		
		if (sideToMove == side) {
			value += 0.5;
		}
		
		return value;
	}
	
	private double getValue(Position position) {
		double value = position.piece.getValue();
		
		switch(position.piece) {
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
		return (double) getAllMoves(position).size() / position.piece.getMaxMoves();
	}

	private double getAttacksFactor(Position position) {
		return (double) getAllAttacks(position).size() / position.piece.getMaxAttacks();
	}

	private double getDefendsFactor(Position position) {
		return (double) getAllDefends(position).size() / position.piece.getMaxAttacks();
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
			.filter(position -> position.side == sideToMove)
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
				.filter(position -> position.side == sideToMove)
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
		return getAnalysis().positionMovesMap.get(position);
	}

	private List<Position> getAllAttacks(Position position) {
		return getAnalysis().positionAttacksMap.get(position);
	}

	private List<Position> getAllDefends(Position position) {
		return getAnalysis().positionDefendsMap.get(position);
	}

	private void addAllMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		switch(position.piece) {
		case Pawn:
			addPawnMoves(position, moves, attacks, defends);
			break;
		case Knight:
			addKnightMoves(position, moves, attacks, defends);
			break;
		case Bishop:
			addBishopMoves(position, moves, attacks, defends);
			break;
		case Rook:
			addRookMoves(position, moves, attacks, defends);
			break;
		case Queen:
			addQueenMoves(position, moves, attacks, defends);
			break;
		case King:
			addKingMoves(position, moves, attacks, defends);
			break;
		}
	}

	private void addPawnMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int direction = getPawnDirection(position.side);
		
		if (addMovePawnIfFree(position, position.x, position.y + direction, moves, attacks, defends) && position.y == getPawnStart(position.side)) {
			addMovePawnIfFree(position, position.x, position.y + direction + direction, moves, attacks, defends);
		}
		addMovePawnMustKill(position, position.x + 1, position.y + direction, moves, attacks, defends);
		addMovePawnMustKill(position, position.x - 1, position.y + direction, moves, attacks, defends);
		
		// TODO add en-passant
	}
	
	private void addKnightMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addMove(position, position.x-2, position.y+1, moves, attacks, defends);
		addMove(position, position.x-1, position.y+2, moves, attacks, defends);
		addMove(position, position.x+1, position.y+2, moves, attacks, defends);
		addMove(position, position.x+2, position.y+1, moves, attacks, defends);
		addMove(position, position.x+2, position.y-1, moves, attacks, defends);
		addMove(position, position.x+1, position.y-2, moves, attacks, defends);
		addMove(position, position.x-1, position.y-2, moves, attacks, defends);
		addMove(position, position.x-2, position.y-1, moves, attacks, defends);
	}

	private void addBishopMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addRayMoves(position, -1, -1, moves, attacks, defends);
		addRayMoves(position, +1, -1, moves, attacks, defends);
		addRayMoves(position, -1, +1, moves, attacks, defends);
		addRayMoves(position, +1, +1, moves, attacks, defends);
	}

	private void addRookMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addRayMoves(position, -1, 0, moves, attacks, defends);
		addRayMoves(position, +1, 0, moves, attacks, defends);
		addRayMoves(position, 0, -1, moves, attacks, defends);
		addRayMoves(position, 0, +1, moves, attacks, defends);
	}

	private void addQueenMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addRayMoves(position, -1, 0, moves, attacks, defends);
		addRayMoves(position, +1, 0, moves, attacks, defends);
		addRayMoves(position, 0, -1, moves, attacks, defends);
		addRayMoves(position, 0, +1, moves, attacks, defends);
		
		addRayMoves(position, -1, -1, moves, attacks, defends);
		addRayMoves(position, +1, -1, moves, attacks, defends);
		addRayMoves(position, -1, +1, moves, attacks, defends);
		addRayMoves(position, +1, +1, moves, attacks, defends);
	}

	private void addRayMoves(Position position, int directionX, int directionY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.x;
		int y = position.y;
		
		boolean ok = false;
		do {
			x += directionX;
			y += directionY;
			Move move = addMove(position, x, y, moves, attacks, defends);
			ok = move != null && move.kill == null;
		} while(ok);
	}
	
	private void addKingMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		addMoveIfSave(position, position.x-1, position.y-1, moves, attacks, defends);
		addMoveIfSave(position, position.x-1, position.y+0, moves, attacks, defends);
		addMoveIfSave(position, position.x-1, position.y+1, moves, attacks, defends);
		addMoveIfSave(position, position.x+0, position.y-1, moves, attacks, defends);
		addMoveIfSave(position, position.x+0, position.y+0, moves, attacks, defends);
		addMoveIfSave(position, position.x+0, position.y+1, moves, attacks, defends);
		addMoveIfSave(position, position.x+1, position.y-1, moves, attacks, defends);
		addMoveIfSave(position, position.x+1, position.y+0, moves, attacks, defends);
		addMoveIfSave(position, position.x+1, position.y-1, moves, attacks, defends);
	}
	
	private Move addMoveIfSave(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		// TODO verify if safe from attack
		return addMove(position, targetX, targetY, moves, attacks, defends);
	}
	
	private boolean addMovePawnIfFree(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target == null) {
			if (targetY == getLastRow(position.side)) {
				for (Piece convert : Arrays.asList(Piece.Knight, Piece.Bishop, Piece.Rook, Piece.Queen)) {
					moves.add(new Move(position, targetX, targetY, target, convert));
				}
			} else {
				Move move = new Move(position, targetX, targetY, target);
				moves.add(move);
			}
			return true;
		}
		return false;
	}

	private boolean addMovePawnMustKill(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return false;
		}
		
		Position target = getPosition(targetX, targetY);
		if (target != null) {
			if (target.side != position.side) {
				if (targetY == getLastRow(position.side)) {
					for (Piece convert : Arrays.asList(Piece.Knight, Piece.Bishop, Piece.Rook, Piece.Queen)) {
						moves.add(new Move(position, targetX, targetY, target, convert));
						attacks.add(target);
					}
				} else {
					moves.add(new Move(position, targetX, targetY, target));
					attacks.add(target);
				}
			} else {
				defends.add(target);
			}
			return true;
		}
		return false;
	}

	private static int getPawnLine(Position position) {
		switch(position.side) {
		case White:
			return position.y;
		case Black:
			return 7 - position.y;
		}
		throw new IllegalArgumentException("Unknown side: " + position.side);
	}

	private static int getPawnDirection(Side side) {
		switch(side) {
		case White:
			return 1;
		case Black:
			return -1;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private static int getPawnStart(Side side) {
		switch(side) {
		case White:
			return 1;
		case Black:
			return 6;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private static int getLastRow(Side side) {
		switch(side) {
		case White:
			return 7;
		case Black:
			return 0;
		}
		throw new IllegalArgumentException("Unknown side: " + side);
	}

	private Move addMove(Position position, int targetX, int targetY, List<Move> moves, List<Position> attacks, List<Position> defends) {
		if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) {
			return null;
		}
		
		Move move = null;
		Position target = getPosition(targetX, targetY);
		if (target == null) {
			move = new Move(position, targetX, targetY, target);
			moves.add(move);
		} else {
			if (target.side != position.side) {
				move = new Move(position, targetX, targetY, target);
				moves.add(move);
				attacks.add(target);
			} else {
				defends.add(target);
			}
		}

		return move;
	}

	public void move(Move move) {
		positions.remove(move.source);
		positions.remove(move.kill);
		
		Piece piece = move.convert == null ? move.source.piece : move.convert;
		Position newPosition = new Position(piece, move.source.side, move.targetX, move.targetY);
		
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
