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

	private final Map<String, Set<EntityValueTuple<String>>> fenToRecommendedMoves = new ConcurrentHashMap<>();

	private final Random random = new Random();
	
	private List<String> lastMoves = new ArrayList<>();
	
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
		String[] moves = line.split("\\s+");
		
		Board board = new Board();
		for (int i = 0; i < moves.length; i++) {
			String move = moves[i];

			if (move.equals("#")) {
				break;
			}
			
			if (move.equals(".")) {
				move = lastMoves.get(i);
				board.move(move);
			} else {
				lastMoves = new ArrayList<>(lastMoves.subList(0, i));
				lastMoves.add(move);
				double probability = 1;
				if (moves.length > i + 1) {
					probability = Double.parseDouble(moves[++i]);
				}
				
				String fen = board.toFenString();
				
				Set<EntityValueTuple<String>> recommendedMoves = fenToRecommendedMoves.computeIfAbsent(fen, (key) -> new HashSet<>());
				recommendedMoves.add(new EntityValueTuple<>(move, probability));
				board.move(move);
				break;
			}
		}
	}

	@Override
	public String bestMove(Board board) {
		String fen = board.toFenString();
		
		Set<EntityValueTuple<String>> recommendedMoves = fenToRecommendedMoves.get(fen);
		
		if (recommendedMoves == null) {
			return null;
		}

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// ignored
		}

		return RandomUtil.pickRandom(random, recommendedMoves);
	}
}
