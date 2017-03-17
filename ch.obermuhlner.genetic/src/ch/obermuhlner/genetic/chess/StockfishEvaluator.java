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
	
	@Override
	public double evaluate(StartPosition first, StartPosition second) {
		Board board = StartPosition.toBoard(first, second);

		double result = 0;
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(chessEngine);
			Process process = processBuilder.start();
			
			BufferedWriter processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
//			processInput.write("uci\n");
			
			processInput.write("position fen ");
			processInput.write(board.toFenString());
			processInput.write(" w -- - 0 1");
			processInput.write("\n");
			
			processInput.write("eval\n");
			
			processInput.flush();
			
			result = readUntilResult(processOutput);

			processInput.write("quit\n");
			processInput.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		
		return 0;
	}
}
