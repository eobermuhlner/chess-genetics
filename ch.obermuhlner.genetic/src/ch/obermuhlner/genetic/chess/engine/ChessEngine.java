package ch.obermuhlner.genetic.chess.engine;

public interface ChessEngine {

	void setInfoLogger(InfoLogger infoLogger);
	
	void setStartPosition();
	
	void setFen(String fen);
	
	boolean isWhiteToMove();
	
	double evaluate();

	CalculationState<String> bestMove(long thinkingMilliseconds);
	
	void move(String move);
	
	interface CalculationState<T> {
		boolean isFinished();
		
		T getResult();
	}
}
