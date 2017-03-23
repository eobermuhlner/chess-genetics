package ch.obermuhlner.genetic.chess.engine;

public class Move {
	private final Position source;
	private final int targetX;
	private final int targetY;
	private final Position kill;
	private final Piece convert;
	private final double value;
	
	public Move(Position source, int targetX, int targetY, Position kill) {
		this(source, targetX, targetY, kill, null);
	}

	public Move(Position source, int targetX, int targetY, Position kill, Piece convert) {
		this.source = source;
		this.targetX = targetX;
		this.targetY = targetY;
		this.kill = kill;
		this.convert = convert;
		
		value = calculateMoveValue();
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
		return value; 
	}
	
	private double calculateMoveValue() {
		double result = 1.0;
		
		result -= source.getPiece().getValue(source.getSide(), source.getX(), source.getY());
		result += source.getPiece().getValue(source.getSide(), targetX, targetY);
		
		if (kill != null) {
			result += kill.getPiece().getValue(kill.getSide(), kill.getX(), kill.getY());
		}
		if (convert != null) {
			result += convert.getValue(source.getSide(), source.getX(), source.getY());
		}
		return result;
	}

	@Override
	public String toString() {
		return toNotationString();
	}
	
	public String toNotationString() {
		StringBuilder result = new StringBuilder();
		result.append(source);
		if (kill != null) {
			result.append("x");
			result.append(kill.getCharacter());
		}
		result.append(Board.toPositionString(targetX, targetY));
		if (convert != null) {
			result.append("=");
			result.append(convert.getCharacter(source.getSide()));
		}
		result.append("(");
		result.append(String.format("%4.3f", getValue()));
		result.append(")");
		
		return result.toString();
	}
}