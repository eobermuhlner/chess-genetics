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

	private final static Pattern EVALUATION_RESULT = Pattern.compile("Total Evaluation: (-?[0-9]+\\.[0-9]*)");
	private final static Pattern BESTMOVE_PONDER_RESULT = Pattern.compile("bestmove (.+) ponder (.+)");
	private final static Pattern BESTMOVE_RESULT = Pattern.compile("bestmove (.+)");
	private static final int MOVE_COUNT = 20;
	private static final int THINKING_TIME = 100;
	
	private final String chessEngine = "C:/Apps/stockfish-8-win/Windows/stockfish_8_x64";

	private BufferedWriter processInput;

	private BufferedReader processOutput;

	public void start() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(chessEngine);
			Process process = processBuilder.start();
			processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

//			processInput.write("uci\n");
//			processInput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			processInput.write("quit\n");
			processInput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public double evaluate(StartPosition first, StartPosition second) {
		if (processInput == null) {
			start();
		}
		
		while(true) {
			try {
				return evaluateInternal(first, second);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private double evaluateInternal(StartPosition white, StartPosition black) throws IOException {
		Board board = StartPosition.toBoard(white, black);
		
		double result = 0;
	
		String position = "position fen " + board.toFenString() + " w -- - 0 ";
		List<String> moves = new ArrayList<>();
		
		sendCommand(position + "1");
		
		for (int moveNumber = 0; moveNumber < MOVE_COUNT; moveNumber++) {
			sendCommand("go movetime " + THINKING_TIME);
			
			List<String> bestmove = readUntilBestMove(processOutput);
			System.out.println("BESTMOVE " + bestmove);
			
			if (bestmove == null) {
				break;
			}
			
			moves.addAll(bestmove);
			
			int halfMoveCount = moves.size() + 1;
			sendCommand(position + halfMoveCount + " moves " + toMovesList(moves));
		}
		
		// go movetime 100
		// -> bestmove d7d5 ponder d2d4
		// ponderhit
		
		sendCommand("eval");
		
		result = readUntilEvaluationResult(processOutput);
		//System.out.println(board.toFenString() + " : " + result);
		
		return result;
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
		System.out.println("COMMAND " + command);
		
		processInput.write(command);
		processInput.write("\n");
		processInput.flush();
	}

	private List<String> readUntilBestMove(BufferedReader processOutput) throws IOException {
		String line = processOutput.readLine();
		while(line != null) {
			System.out.println("LINE " + line);

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
			System.out.println("LINE " + line);
			Matcher matcher = EVALUATION_RESULT.matcher(line);
			if (matcher.find()) {
				String found = matcher.group(1);
				return Double.parseDouble(found);
			}
			
			line = processOutput.readLine();
		}
		
		return 0;
	}

}
