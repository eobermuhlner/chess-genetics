package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Board {

	private static final char[] LETTERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };

	private final InfoLogger infoLogger;
	
	private final List<Position> positions = new ArrayList<>();
	
	private Side sideToMove = Side.White;
	
	private Analysis analysis;

	public Board() {
		this(new InfoLogger() {
			public void infoString(String message) {
				System.out.println(message);
			}
		});
	}
	
	public Board(InfoLogger infoLogger) {
		this.infoLogger = infoLogger;
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
		String[] splitFen = fen.split(" +");
		List<Position> fenPositions = toFenPositions(splitFen[0]);
		Side fenSide = Side.White;
		if (splitFen.length >= 2) {
			fenSide = splitFen[1].equals("w") ? Side.White : Side.Black;
		}
		
		clear();
		positions.addAll(fenPositions);
		sideToMove = fenSide;
		
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

	public List<Position> getPositions() {
		return positions;
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
	
	public boolean isFinished() {
		return getAllMoves().isEmpty();
	}
	
	public boolean isMate() {
		return isCheck() && getAllMoves().isEmpty();
	}
	
	public boolean isPatt() {
		return !isCheck() && getAllMoves().isEmpty();
	}
	
	public boolean isCheck() {
		return getAnalysis().isKingInCheck();
	}
	
	public Position getPosition(int x, int y) {
		return getAnalysis().getPosition(x, y);
	}
	
	public double getValue(Position position) {
		return getAnalysis().getValue(position);
	}
	
	public double getValue(Move move) {
		return getAnalysis().getValue(move); // TODO
	}
	
	public double getValue() {
		return getSideValue(Side.White) - getSideValue(Side.Black);
	}
	
	public double getSideValue(Side side) {
		double value = 0;
		
		if (sideToMove == side) {
			if (isMate()) {
				return 100;
			}
			if (isPatt()) {
				return 0;
			}

			if (isCheck()) {
				value += 20;
			} else {
				value += 0.5;
			}
		} else {
			if (isMate() || isPatt()) {
				return 0;
			}
		}

		value += positions.stream()
				.filter(position -> position.getSide() == side)
				.mapToDouble(position -> getAnalysis().getValue(position))
				.sum();
		
		
		return value;
	}
	
	public List<Move> getAllMoves() {
		if (isCheck()) {
			return getAllMovesUnderCheck();
		}
		
		return getAllMovesNormal();
	}
	
	private List<Move> getAllMovesUnderCheck() {
		List<Move> moves = new ArrayList<>();
		
		moves.addAll(positions.stream()
			.filter(position -> position.getSide() == sideToMove && position.getPiece() == Piece.King)
			.flatMap(position -> getAnalysis().getMoves(position).stream())
			.filter(move -> !isStillInCheck(move))
			.collect(Collectors.toList()));
		
		// TODO find moves that kill checking piece
		// TODO find moves that intercept checking move
		
		return moves;
	}

	private boolean isStillInCheck(Move move) {
		return getAnalysis().getAttackers(move.getSource()).stream()
			.anyMatch(position -> {
				return canAttackTo(position, move.getTargetX(), move.getTargetY());
			});
	}

	private boolean canAttackTo(Position position, int targetX, int targetY) {
		int deltaX = position.getX() - targetX;
		int deltaY = position.getY() - targetY;

		switch(position.getPiece()) {
		case Bishop:
			return (deltaX == deltaY) || (deltaX == -deltaY);
		case Queen:
			return (deltaX == 0 && deltaY != 0) || (deltaX != 0 && deltaY == 0) || (deltaX == deltaY) || (deltaX == -deltaY);
		case Rook:
			return (deltaX == 0 && deltaY != 0) || (deltaX != 0 && deltaY == 0);
		case King:
			return (deltaX >= -1 && deltaX <= 1) && (deltaY >= -1 && deltaX <= 1);
		case Pawn:
			return (deltaX == -1 || deltaX == 1) && deltaY == getPawnDirection(position.getSide());
		default:
			// TODO implement missing pieces
			return false;
			//throw new UnsupportedOperationException("Not implemented");
		}
	}

	private List<Move> getAllMovesNormal() {
		return positions.stream()
				.filter(position -> position.getSide() == sideToMove && !moveWillLeaveInCheck(position))
				.flatMap(position -> getAnalysis().getMoves(position).stream())
				.collect(Collectors.toList());
	}
	
	private boolean moveWillLeaveInCheck(Position position) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO validate input
		//CheckArgument.isTrue(move.getKill() == null || move.getKill().getPiece() != Piece.King, () -> "King cannot be killed: " + move);
		
		Position source = move.getSource();
		
		positions.remove(source);
		positions.remove(move.getKill());

		if (source.getPiece() == Piece.King && move.getKill() != null && move.getKill().getPiece() == Piece.Rook && source.getSide() == move.getKill().getSide()) {
			// castling (rochade)
			int kingDirectionX = move.getKill().getX() > source.getX() ? +1 : -1;

			Position newKingPosition = new Position(Piece.King, source.getSide(), source.getX() + kingDirectionX*2, source.getY());
			positions.add(newKingPosition);
			
			Position newRookPosition = new Position(Piece.Rook, source.getSide(), source.getX() + kingDirectionX, source.getY());
			positions.add(newRookPosition);
		} else {
			// normal move (including conversion of pawn)
			Piece piece = move.getConvert() == null ? source.getPiece() : move.getConvert();
			Position newPosition = new Position(piece, source.getSide(), move.getTargetX(), move.getTargetY());
			positions.add(newPosition);
		}
		
		sideToMove = sideToMove.otherSide();

		invalidateAnalysis();
	}

	public void move(String move) {
		char[] chars = move.toCharArray();
		Piece convert = null;
		if (chars.length >= 5) {
			convert = Piece.ofCharacter(chars[4]);
		}
		move(
				letterToInt(chars[0]),
				Character.getNumericValue(chars[1]) - 1,
				letterToInt(chars[2]),
				Character.getNumericValue(chars[3]) - 1,
				convert);
	}
	
	public void move(int sourceX, int sourceY, int targetX, int targetY) {
		move(sourceX, sourceY, targetX, targetY, null);
	}

	public void move(int sourceX, int sourceY, int targetX, int targetY, Piece convert) {
		Position source = positions.stream()
				.filter(position -> position.getX() == sourceX && position.getY() == sourceY)
				.findAny()
				.get();
		Position target = positions.stream()
				.filter(position -> position.getX() == targetX && position.getY() == targetY)
				.findAny()
				.orElse(null);

		move(new Move(source, targetX, targetY, target, convert));
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
		
		builder.append(" ");
		builder.append(sideToMove == Side.White ? "w" : "b");
		
		return builder.toString();
	}

	public static String toPositionString(int x, int y) {
		return String.valueOf(LETTERS[x]) + (y + 1);
	}
	
	public static void main(String[] args) {
		Random random = new Random(1234);
		
		Board board = new Board();
		board.setStartPosition();

		int index = 1;
		while (!board.isFinished()) {
			System.out.println("STEP  " + index++);
			System.out.println("FEN   " + board.toFenString());
			System.out.println("STATE " + board.getSideToMove() + " " + (board.isMate() ? "mate " : "") + (board.isCheck() ? "check " : "") + (board.isPatt() ? "patt" : ""));
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
