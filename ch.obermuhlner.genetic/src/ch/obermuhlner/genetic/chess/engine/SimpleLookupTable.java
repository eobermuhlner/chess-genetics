package ch.obermuhlner.genetic.chess.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleLookupTable implements LookupTable {

	private final Map<String, Set<String>> fenToRecommendedMoves = new ConcurrentHashMap<>();

	private final Random random = new Random();
	
	public SimpleLookupTable() {
	}
	
	public void load(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while (line != null) {
				if (!line.isEmpty() && !line.startsWith("#")) {
					parseLine(line);
				}
				
				line = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseLine(String line) {
		String[] split = line.split(" +");
		
		Board board = new Board();
		for (String move : split) {
			if (move.equals(":")) {
				break;
			}
			
			String fen = board.toFenString();
			
			Set<String> recommendedMoves = fenToRecommendedMoves.computeIfAbsent(fen, (key) -> new HashSet<>());
			recommendedMoves.add(move);
			
			board.move(move);
		}
	}

	@Override
	public String bestMove(Board board) {
		String fen = board.toFenString();
		
		Set<String> recommendedMoves = fenToRecommendedMoves.get(fen);
		
		if (recommendedMoves == null) {
			return null;
		}

		List<String> recommendedMovesList = new ArrayList<>(recommendedMoves);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// ignored
		}
		
		return recommendedMovesList.get(random.nextInt(recommendedMovesList.size()));
	}
}
