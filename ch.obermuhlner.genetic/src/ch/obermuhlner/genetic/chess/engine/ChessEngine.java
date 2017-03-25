package ch.obermuhlner.genetic.chess.engine;

public interface ChessEngine {

	void setInfoLogger(InfoLogger infoLogger);
	
	void setStartPosition();
	
	void setFen(String fen);
	
	double evaluate();

	String bestMove(long thinkingMilliseconds);
	
	void move(String move);
}
