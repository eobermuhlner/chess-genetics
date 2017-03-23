package ch.obermuhlner.genetic.chess.engine;

public interface ChessEngine {

	void setStartPosition();
	
	void setFen(String fen);
	
	double evaluate();

	String bestMove(long thinkingMilliseconds);
	
	void move(String move);

}
