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
	private static final Pattern BESTMOVE_RESULT = Pattern.compile("bestmove (\\S+)");
	private static final boolean PRINT_DEBUG = false;
	
	private final String chessEngine = "C:/Apps/stockfish-8-win/Windows/stockfish_8_x64";

	private final int moveCount;
	private final int thinkingTime;

	private BufferedWriter processInput;
	private BufferedReader processOutput;

	public StockfishPlayEvaluator() {
		this(10, 5);
	}
	
	public StockfishPlayEvaluator(int moveCount, int thinkingTime) {
		this.moveCount = moveCount;
		this.thinkingTime = thinkingTime;
	}

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
		double valueFirstAsWhite = evaluatePlay(first, second, moveCount, thinkingTime);
		double valueFirstAsBlack = evaluatePlay(second, first, moveCount, thinkingTime);
		return valueFirstAsWhite - valueFirstAsBlack;
	}
	
	public double evaluatePlay(StartPosition white, StartPosition black, int moveCount, int thinkingTime) {
		return execute(() -> {
			Board board = StartPosition.toBoard(white, black);
			
			double result = 0;
		
			//sendCommand("ucinewgame");
			
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
			
//			Matcher matcher = BESTMOVE_PONDER_RESULT.matcher(line);
//			if (matcher.find()) {
//				return Arrays.asList(matcher.group(1), matcher.group(2));
//			}
//
			Matcher  matcher = BESTMOVE_RESULT.matcher(line);
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
		//StartPosition white = new StandardStartPositionFactory().createGenom();
		StartPosition white = new StartPosition("4n3/kp3qr1/pbp1nbr1/1ppppp2");
		StartPosition black = new StartPosition("1k2n3/1p3qr1/pbp1nbr1/1ppppp2");
		System.out.println(StartPosition.toBoard(white, white).toFenString());

		StockfishPlayEvaluator evaluator = new StockfishPlayEvaluator();

		System.out.println("EVAL " + evaluator.evaluatePlay(white, black, 0, 0));

		int n = 100;
		double total = 0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double values[] = new double[n];
		
		for (int i = 0; i < n; i++) {
			double value = evaluator.evaluatePlay(white, black, 1000, 10);
			System.out.println("VALUE " + value);

			values[i] = value;
			total += value;
			min = Math.min(min, value);
			max = Math.max(max, value);
		}
		
		double average = total / n;
		double totalSquareDeviations = 0;
		for (int i = 0; i < values.length; i++) {
			double deviation = average - values[i];
			totalSquareDeviations += deviation * deviation;
		}
		double variance = totalSquareDeviations / n;
		double stddev = Math.sqrt(variance);
		
		System.out.println("MIN " + min);
		System.out.println("MAX " + max);
		System.out.println("AVG " + average);
		System.out.println("stddev " + stddev);
	}
}
