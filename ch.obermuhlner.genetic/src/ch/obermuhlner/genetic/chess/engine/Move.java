package ch.obermuhlner.genetic.chess.engine;

public class Move {
	private final Position source;
	private final int targetX;
	private final int targetY;
	private final Position kill;
	private final Piece convert;
	
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
	
	public int getTargetX() {
		return targetX;
	}
	
	public int getTargetY() {
		return targetY;
	}

	public Position getKill() {
		return kill;
	}
	
	public Piece getConvert() {
		return convert;
	}

	public String getTargetPositionString() {
		return Board.toPositionString(targetX, targetY);
	}
	
	public double getValue() {
		if (kill != null) {
			return kill.getPiece().getValue();
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
		result.append(Board.toPositionString(targetX, targetY));
		if (kill != null) {
			result.append("x");
			result.append(kill);
		}
		if (convert != null) {
			result.append("=");
			result.append(convert.getCharacter(source.getSide()));
		}
		result.append("(");
		result.append(getValue());
		result.append(")");
		
		return result.toString();
	}
}