package ch.obermuhlner.genetic.chess.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analysis {
	private final Position[] positionBoard = new Position[64];
	private final Map<Position, List<Move>> positionMovesMap = new HashMap<>();
	private final Map<Position, List<Position>> positionAttacksMap = new HashMap<>();
	private final Map<Position, List<Position>> positionDefendsMap = new HashMap<>();

	public Analysis(Board board) {
		for (Position position : board.getPositions()) {
			positionBoard[position.getX() + position.getY() * 8] = position;
		}
		
		for (Position position : board.getPositions()) {
			List<Move> moves = new ArrayList<>();
			List<Position> attacks = new ArrayList<>();
			List<Position> defends = new ArrayList<>();
			
			addAllMoves(position, moves, attacks, defends);
			
			positionMovesMap.put(position, moves);
			positionAttacksMap.put(position, attacks);
			positionDefendsMap.put(position, defends);
		}
	}
	
	public Position getPosition(int x, int y) {
		return positionBoard[x + y * 8];
	}
	
	public Map<Position, List<Move>> getPositionMovesMap() {
		return positionMovesMap;
	}
	
	public Map<Position, List<Position>> getPositionAttacksMap() {
		return positionAttacksMap;
	}
	
	public Map<Position, List<Position>> getPositionDefendsMap() {
		return positionDefendsMap;
	}
	
	private void addAllMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		switch(position.getPiece()) {
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
		int x = position.getX();
		int y = position.getY();

		int direction = Board.getPawnDirection(position.getSide());
		
		if (addMovePawnIfFree(position, x, y + direction, moves, attacks, defends) && y == Board.getPawnStart(position.getSide())) {
			addMovePawnIfFree(position, x, y + direction + direction, moves, attacks, defends);
		}
		addMovePawnMustKill(position, x+1, y + direction, moves, attacks, defends);
		addMovePawnMustKill(position, x-1, y + direction, moves, attacks, defends);
		
		// TODO add en-passant
	}
	
	private void addKnightMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.getX();
		int y = position.getY();

		addMove(position, x-2, y+1, moves, attacks, defends);
		addMove(position, x-1, y+2, moves, attacks, defends);
		addMove(position, x+1, y+2, moves, attacks, defends);
		addMove(position, x+2, y+1, moves, attacks, defends);
		addMove(position, x+2, y-1, moves, attacks, defends);
		addMove(position, x+1, y-2, moves, attacks, defends);
		addMove(position, x-1, y-2, moves, attacks, defends);
		addMove(position, x-2, y-1, moves, attacks, defends);
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
		int x = position.getX();
		int y = position.getY();
		
		boolean ok = false;
		do {
			x += directionX;
			y += directionY;
			Move move = addMove(position, x, y, moves, attacks, defends);
			ok = move != null && move.getKill() == null;
		} while(ok);
	}
	
	private void addKingMoves(Position position, List<Move> moves, List<Position> attacks, List<Position> defends) {
		int x = position.getX();
		int y = position.getY();

		addMoveIfSave(position, x-1, y-1, moves, attacks, defends);
		addMoveIfSave(position, x-1, y+0, moves, attacks, defends);
		addMoveIfSave(position, x-1, y+1, moves, attacks, defends);
		addMoveIfSave(position, x+0, y-1, moves, attacks, defends);
		addMoveIfSave(position, x+0, y+0, moves, attacks, defends);
		addMoveIfSave(position, x+0, y+1, moves, attacks, defends);
		addMoveIfSave(position, x+1, y-1, moves, attacks, defends);
		addMoveIfSave(position, x+1, y+0, moves, attacks, defends);
		addMoveIfSave(position, x+1, y-1, moves, attacks, defends);
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
			if (targetY == Board.getLastRow(position.getSide())) {
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
			if (target.getSide() != position.getSide()) {
				if (targetY == Board.getLastRow(position.getSide())) {
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
			if (target.getSide() != position.getSide()) {
				move = new Move(position, targetX, targetY, target);
				moves.add(move);
				attacks.add(target);
			} else {
				defends.add(target);
			}
		}

		return move;
	}
}