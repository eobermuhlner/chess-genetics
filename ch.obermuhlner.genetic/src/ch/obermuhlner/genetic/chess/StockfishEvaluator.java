package ch.obermuhlner.genetic.chess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.obermuhlner.genetic.GenomeEvaluator;

public class StockfishEvaluator implements GenomeEvaluator<StartPosition> {

	private final static Pattern EVALUATION_RESULT_PATTERN = Pattern.compile("Total Evaluation: (-?[0-9]+\\.[0-9]*)");
	
	private final String chessEngine = "C:/Apps/stockfish-8-win/Windows/stockfish_8_x64";

	private BufferedWriter processInput;

	private BufferedReader processOutput;

	private BufferedReader processError;

	public void start() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(chessEngine);
			Process process = processBuilder.start();
			processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

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
		while(true) {
			try {
				return evaluateInternal(first, second);
			} catch (IOException e) {
				//e.printStackTrace();
				start();
			}
		}
	}
	
	private double evaluateInternal(StartPosition white, StartPosition black) throws IOException {
		Board board = StartPosition.toBoard(white, black);

		double result = 0;
		
		processInput.write("position fen ");
		processInput.write(board.toFenString());
		processInput.write(" w -- - 0 1");
		processInput.write("\n");
		processInput.flush();
		
		processInput.write("eval\n");
		processInput.flush();
		
		result = readUntilResult(processOutput);
		//System.out.println(board.toFenString() + " : " + result);
		
		return result;
	}

	private double readUntilResult(BufferedReader processOutput) throws IOException {
		String line = processOutput.readLine();
		while(line != null) {
			Matcher matcher = EVALUATION_RESULT_PATTERN.matcher(line);
			if (matcher.find()) {
				String found = matcher.group(1);
				return Double.parseDouble(found);
			}
			
			line = processOutput.readLine();
		}
		
		line = processError.readLine();
		while(line != null) {
			System.out.println("ERR: " + line);
			line = processError.readLine();
		}
		
		return 0;
	}
}
