package ch.obermuhlner.genetic.chess.engine.uci;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import ch.obermuhlner.genetic.chess.engine.ChessEngine;
import ch.obermuhlner.genetic.chess.engine.InfoLogger;
import ch.obermuhlner.genetic.chess.engine.MonteCarloChessEngine;

public class UciProtocol implements InfoLogger {

	private final BufferedReader in;
	private final PrintWriter out;
	private PrintWriter log;
	private final ChessEngine chessEngine;

	private volatile boolean stop;
	
	public UciProtocol(ChessEngine chessEngine) {
		this(System.in, System.out, chessEngine);
		chessEngine.setInfoLogger(this);
	}
	
	public UciProtocol(InputStream inputStream, OutputStream outputStream, ChessEngine chessEngine) {
		in = new BufferedReader(new InputStreamReader(inputStream));
		out = new PrintWriter(outputStream, true);
		
		try {
			log = new PrintWriter(new FileWriter("guppy_log.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.chessEngine = chessEngine;
	}
	
	public void run() {
		try {
			String line = in.readLine();
			while (line != null) {
				if (line.isEmpty()) {
					continue;
				}
				if (log != null) {
					log.println("IN  " + line);
					log.flush();
				}
				
				String[] args = line.split(" +");
				if (args.length > 0) {
					try {
						execute(args);
					} catch(Exception ex) {
						if (log != null) {
							ex.printStackTrace(log);
						}
					}
				}
				
				line = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void execute(String[] args) {
		switch(args[0]) {
		case "quit":
			System.exit(0);
			break;
		case "stop":
			stop = true;
			break;
		case "uci":
			executeUci(args);
			break;
		case "ucinewgame":
			executeUcinewgame(args);
			break;
		case "isready":
			executeIsready(args);
			break;
		case "position":
			executePosition(args);
			break;
		case "go":
			executeGo(args);
			break;
		default:
			println("Unknown command: " + Arrays.toString(args));
		}
	}

	private void executeUci(String[] args) {
		println("id name guppy 0.1");
		println("id author Eric Obermuhlner");
		println("uciok");
	}

	private void executeUcinewgame(String[] args) {
		// does nothing
	}
	
	private void executeIsready(String[] args) {
		println("readyok");
	}

	private void executeGo(String[] args) {
		long thinkingMilliseconds = 1000;
		String bestMove = chessEngine.bestMove(thinkingMilliseconds);
		println("bestmove " + bestMove);
	}

	private void executePosition(String[] args) {
		int argIndex = 1;
		while (argIndex < args.length) {
			switch(args[argIndex]) {
			case "startpos":
				chessEngine.setStartPosition();
				break;
			case "fen":
				String fen = "";
				for (int i = 0; i < 6; i++) {
					fen += args[++argIndex] + " ";
				}
				chessEngine.setFen(fen);
				break;
			case "moves":
				argIndex++;
				while (argIndex < args.length) {
					chessEngine.move(args[argIndex++]);
				}
				break;
			default:
				println("Unknown position option: " + args[argIndex]);
			}
			
			argIndex++;
		}
	}

	@Override
	public void infoString(String message) {
		println("info string " + message);
	}
	
	private void println(String message) {
		if (log != null) {
			log.println("OUT " + message);
			log.flush();
		}
		out.println(message);
	}

	public static void main(String[] args) {
		//UciProtocol uciProtocol = new UciProtocol(new DebugChessEngine());
		UciProtocol uciProtocol = new UciProtocol(new MonteCarloChessEngine());
		
		uciProtocol.run();
	}

}
