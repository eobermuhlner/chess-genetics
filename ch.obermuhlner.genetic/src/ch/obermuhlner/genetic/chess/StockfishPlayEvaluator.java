package ch.obermuhlner.genetic.chess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.obermuhlner.genetic.GenomeEvaluator;

public class StockfishPlayEvaluator implements GenomeEvaluator<StartPosition> {

	private static final Pattern EVALUATION_RESULT = Pattern.compile("Total Evaluation: (-?[0-9]+\\.[0-9]*)");
	private static final Pattern BESTMOVE_PONDER_RESULT = Pattern.compile("bestmove (.+) ponder (.+)");
	private static final Pattern BESTMOVE_RESULT = Pattern.compile("bestmove (.+)");
	private static final boolean PRINT_DEBUG = false;
	
	private final String chessEngine = "C:/Apps/stockfish-8-win/Windows/stockfish_8_x64";

	private int moveCount = 10;
	private int thinkingTime = 5;

	private BufferedWriter processInput;
	private BufferedReader processOutput;

	public void start() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(chessEngine);
			Process process = processBuilder.start();
			processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			sendCommand("uci");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			sendCommand("quit");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public double evaluate(StartPosition first, StartPosition second) {
		return evaluatePlay(first, second, moveCount, thinkingTime);
	}
	
	public double evaluatePlay(StartPosition white, StartPosition black, int moveCount, int thinkingTime) {
		return execute(() -> {
			Board board = StartPosition.toBoard(white, black);
			
			double result = 0;
		
			sendCommand("ucinewgame");
			
			String position = "position fen " + board.toFenString() + " w -- - 0 ";
			List<String> moves = new ArrayList<>();
			
			sendCommand(position + "1");
			
			for (int moveNumber = 0; moveNumber < moveCount; moveNumber++) {
				sendCommand("go movetime " + thinkingTime);
				
				List<String> bestmove = readUntilBestMove(processOutput);
				
				if (bestmove == null) {
					return mateValue(moves.size());
				}
				
				moves.addAll(bestmove);
				
				int halfMoveCount = moves.size() + 1;
				sendCommand(position + halfMoveCount + " moves " + toMovesList(moves));
			}
			
			sendCommand("eval");
			
			result = readUntilEvaluationResult(processOutput);
//			System.out.println(board.toFenString() + " : " + result);
			
			return result;
		});
	}
	
	private double mateValue(int movesUntilMate) {
		double decreasingFactor = Math.pow(0.99, movesUntilMate);
		if (movesUntilMate % 2 == 0) {
			return -100 * decreasingFactor;
		} else {
			return 100 * decreasingFactor;
		}
	}

	private String toMovesList(List<String> moves) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < moves.size(); i++) {
			if (i != 0) {
				builder.append(" ");
			}
			builder.append(moves.get(i));
		}
		
		return builder.toString();
	}

	private void sendCommand(String command) throws IOException {
		if (PRINT_DEBUG) {
			System.out.println("COMMAND " + command);
		}
		
		processInput.write(command);
		processInput.write("\n");
		processInput.flush();
	}

	private List<String> readUntilBestMove(BufferedReader processOutput) throws IOException {
		String line = processOutput.readLine();
		while(line != null) {
			if (PRINT_DEBUG) {
				System.out.println("LINE " + line);
			}

			if (line.equals("bestmove (none)")) {
				return null;
			}
			
			Matcher matcher = BESTMOVE_PONDER_RESULT.matcher(line);
			if (matcher.find()) {
				return Arrays.asList(matcher.group(1), matcher.group(2));
			}

			matcher = BESTMOVE_RESULT.matcher(line);
			if (matcher.find()) {
				return Arrays.asList(matcher.group(1));
			}
			
			line = processOutput.readLine();
		}
		
		return null;
	}
	
	private double readUntilEvaluationResult(BufferedReader processOutput) throws IOException {
		String line = processOutput.readLine();
		while(line != null) {
			if (PRINT_DEBUG) {
				System.out.println("LINE " + line);
			}
			Matcher matcher = EVALUATION_RESULT.matcher(line);
			if (matcher.find()) {
				String found = matcher.group(1);
				return Double.parseDouble(found);
			}
			
			line = processOutput.readLine();
		}
		
		return 0;
	}

	private double execute(StockfishExecution function) {
		if (processInput == null) {
			start();
		}
		
		while(true) {
			try {
				return function.execute();
			} catch (IOException e) {
				start();
				return 0;
				//throw new RuntimeException(e);
			}
		}
	}
	
	private interface StockfishExecution {
		double execute() throws IOException;
	}
	
	public static void main(String[] args) {
		StartPosition white = new StandardStartPositionFactory().createGenom();
		//StartPosition black = new StandardStartPositionFactory().createGenom();
		StartPosition black = new StartPosition("5n2/p1pprb1p/pp1n1pb1/1prk1q2");
		System.out.println(StartPosition.toBoard(white, black).toFenString());
		
		StockfishPlayEvaluator evaluator = new StockfishPlayEvaluator();
		double total = 0;
		int n = 10;
		for (int i = 0; i < n; i++) {
			double value = evaluator.evaluatePlay(white, black, 1000, 20);
			total += value;
			System.out.println("VALUE " + value);
		}
		
		System.out.println("AVERAGE " + (total / n));
		System.out.println("EVAL " + evaluator.evaluatePlay(white, black, 1, 0));
	}
}
