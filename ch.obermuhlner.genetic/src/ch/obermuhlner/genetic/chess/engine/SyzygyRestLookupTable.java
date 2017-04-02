package ch.obermuhlner.genetic.chess.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://syzygy-tables.info/api/v2?fen=4k1r1/8/8/8/8/8/3K4/2Q5%20b%20-%20-%200%201
public class SyzygyRestLookupTable implements LookupTable {

	private static final Pattern BEST_MOVE_PATTERN = Pattern.compile("\"bestmove\"\\s*:\\s*\"([a-h1-8nbrq]+)\""); 
	
	@Override
	public String bestMove(Board board, InfoLogger infoLogger) {
		String fen = board.toFenString();
		String json = getHttp(escapeUrl("http://syzygy-tables.info/api/v2?fen=" + fen));
		if (json == null) {
			return null;
		}
		
		//infoLogger.info("string syzygy json " + json.replace('\n', ' '));
		String bestMove = getJsonBestMove(json);
		//infoLogger.info("string syzygy best " + bestMove);
		return bestMove;
	}

	private String getHttp(String url) {
		try {
			StringBuilder result = new StringBuilder();
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
			}
			return result.toString();
		} catch (ProtocolException e) {
			// ignore
		} catch (MalformedURLException e) {
			// ignore
		} catch (IOException e) {
			// ignore
		}
		return null;
	}

	private static String escapeUrl(String string) {
		return string.replaceAll(" ", "%20");
	}

	/*
{
  "bestmove": "e8d7",
  "dtm": -52,
  "dtz": -44,
  "moves": {
    "e8d7": {
      "dtm": 51,
      "dtz": 43,
      "wdl": 2
    },
    "e8d8": {
      "dtm": 49,
      "dtz": 37,
      "wdl": 2
    },
	 */
	private String getJsonBestMove(String json) {
		try (BufferedReader reader = new BufferedReader(new StringReader(json))) {
			String line = reader.readLine();
			while (line != null) {
				Matcher matcher = BEST_MOVE_PATTERN.matcher(line);
				if (matcher.find()) {
					return matcher.group(1);
				}
				
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		SyzygyRestLookupTable lookupTable = new SyzygyRestLookupTable();
		
		Board board = new Board();
		//board.setFenString("4k1r1/8/8/8/8/8/3K4/2Q5 b - - 0 1");
		board.setFenString("8/5r2/6k1/6Q1/6K1/8/8/8 b - - 20 11");
		
		System.out.println(board.toFenString());
		
		String bestMove = lookupTable.bestMove(board, new InfoLogger() {
			@Override
			public void info(String message) {
				System.out.println(message);
			}
		});
		System.out.println(bestMove);
	}
}
