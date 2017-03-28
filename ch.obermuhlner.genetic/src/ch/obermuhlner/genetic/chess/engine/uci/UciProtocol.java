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
import ch.obermuhlner.genetic.chess.engine.ChessEngine.CalculationState;
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
			System.out.println("Stopping");
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
		long thinkingMilliseconds = calculateThinkingTime(args);
		
		if (thinkingMilliseconds == 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		
		stop = false;
		CalculationState<String> calculateBestMove = chessEngine.bestMove(thinkingMilliseconds);
		new Thread(() -> {
			while (!calculateBestMove.isFinished() && !stop) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			String bestMove = calculateBestMove.getResult();
			println("bestmove " + bestMove);
		}).start();
	}

	private long calculateThinkingTime(String[] args) {
		long whiteTime = -1;
		long blackTime = -1;
		long moveTime = -1;
		int movesToGo = -1;
		
		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
			case "wtime":
				whiteTime = Long.parseLong(args[++i]);
				break;
			case "btime":
				blackTime = Long.parseLong(args[++i]);
				break;
			case "movestogo":
				movesToGo = Integer.parseInt(args[++i]);
				break;
			case "movetime":
				moveTime = Long.parseLong(args[++i]);
				break;
			case "depth":
				moveTime = Integer.parseInt(args[++i]) * 100;
				break;
			case "infinity":
				moveTime = Integer.MAX_VALUE;
				break;
			}
		}

		if (moveTime >= 0) {
			return moveTime;
		}
		
		if (whiteTime >= 0 && blackTime >= 0) {
			boolean whiteToMove = chessEngine.isWhiteToMove();
			moveTime = whiteToMove ? whiteTime : blackTime;
			if (movesToGo < 0) {
				movesToGo = 40;
			}
			moveTime = (int) ((moveTime / 2.0) / (movesToGo / 4.0));
		}
		
		if (moveTime < 0) {
			moveTime = 5000;
		}

		return moveTime;
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
