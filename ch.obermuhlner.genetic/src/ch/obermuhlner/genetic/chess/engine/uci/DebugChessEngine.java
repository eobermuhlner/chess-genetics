package ch.obermuhlner.genetic.chess.engine.uci;

import ch.obermuhlner.genetic.chess.engine.ChessEngine;
import ch.obermuhlner.genetic.chess.engine.InfoLogger;

public class DebugChessEngine implements ChessEngine {

	@Override
	public void setInfoLogger(InfoLogger infoLogger) {
		// ignore
	}
	
	@Override
	public void setStartPosition() {
		System.out.println("debug startposition" );
	}

	@Override
	public void setFen(String fen) {
		System.out.println("debug fen " + fen);
	}

	@Override
	public double evaluate() {
		System.out.println("debug eval ");
		return 0;
	}

	@Override
	public String bestMove(long thinkingMilliseconds) {
		System.out.println("debug bestmove " + thinkingMilliseconds);
		return "debug e2e4";
	}

	@Override
	public void move(String move) {
		System.out.println("debug move " + move);
	}

}
