
//
//This class classifies patents as a database invention, or an invention of something else using Paul Grahams spam v ham method
//In this first version, we consider the words (tokens) in the title.
//Author: Ronnie Ward and Josiah Coad
//

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Classifier {
	HashSet<String> hset = new HashSet<String>(); // will be the set of
													// stopwords
	HashMap<String, DocWord> dict = new HashMap<String, DocWord>();// dictionary
																	// from
																	// training
																	// set

	public Classifier() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("stopwords.txt"));
			String str;
			while ((str = in.readLine()) != null) {
				hset.add(str.toLowerCase().trim());
			}
			in.close();
		} catch (IOException e) {
			System.out.println("Error reading stopwords file.");
		}
	}

	public void train(List<String> yesDBList, List<String> noDBList) {
		for (String yesDB : yesDBList) {
			for (String word : tokenizeText(yesDB)) {
				processToken(word, true);
			}
		}
		System.out.println("YesWord Dictionary size: " + dict.size());
		for (String noDB : noDBList) {
			for (String word : tokenizeText(noDB)) {
				processToken(word, false);
			}
		}
		System.out.println("Total Dictionary size: " + dict.size());

		// compute word probabilities
		Set<String> keys = dict.keySet();
		for (String k : keys) {
			DocWord entry = dict.get(k);
			entry.yesFrac = entry.yesCnt / yesDBList.size(); // avg freq per doc type
			entry.noFrac = entry.noCnt / noDBList.size();
			entry.yesProb = entry.yesFrac / (entry.yesFrac + entry.noFrac);
			if (entry.yesProb < 0.01)
				entry.yesProb = 0.01D;
			else if (entry.yesProb > 0.99)
				entry.yesProb = 0.99D;
			entry.noProb = 1.0D - entry.yesProb;
			
			dict.replace(k, entry);
		}
	}

	private void processToken(String token, boolean patType) {
		if (!dict.containsKey(token)) {// is token in the dictionary?
			// add it
			DocWord entry = new DocWord();
			entry.word = token;
			entry.yesCnt = 0;
			entry.noCnt = 0;
			dict.put(token, entry);
		}
		DocWord entry = dict.get(token);
		if (patType == true)
			entry.yesCnt++;
		else
			entry.noCnt++;
		dict.replace(token, entry);
	}

	public double classify(String p) throws Exception {
		HashSet<String> tset = new HashSet<String>();
		ArrayList<DocWord> words = new ArrayList<DocWord>();// words in new
															// patent we are
															// classifying
		String[] tokens = tokenizeText(p);
		for (int i = 0; i < tokens.length; i++) {
			if (dict.containsKey(tokens[i]) && !tset.contains(tokens[i])) {
				// the token is in the dictionary and this is 1st time we have
				// seen it in the patent
				tset.add(tokens[i]); // remember that we have seen this word
				words.add(dict.get(tokens[i]));
			}
			// else //it is an unknown word (not in dict) so just skip it for
			// now
		}
		System.out.println("Patent word count: " + words.size());
		// determine yes or no according to:
		// http://www.paulgraham.com/naivebayes.html
		double yesProduct = 1.0D;
		double noProduct = 1.0D;
		for (DocWord dw : words) {
			yesProduct *= dw.yesProb;
			noProduct *= dw.noProb;
		}
		if (Double.isNaN(yesProduct))
			throw new Exception();
		if (Double.isNaN(noProduct))
			throw new Exception();
		double yesProb = yesProduct / (yesProduct + noProduct);
		if (Double.isNaN(yesProb))
			throw new Exception();
		return yesProb;
	}

	private String[] tokenizeText(String content) {
		content = content.replace('.', ' ').toLowerCase().trim(); // take out
																	// periods
																	// which
																	// could be
																	// used in
																	// things
																	// like
																	// e.g., or
																	// i.e.
		String[] tokens = content.split("[\\,\\s!;?:\"]+"); // split and take
															// out
															// whitespace and
															// other word
															// separators (leave
															// apostrophe)
		List<String> words = new ArrayList<String>();
		for (String token : tokens) {
			if (token.chars().allMatch(Character::isLetter) && !hset.contains(token)) {
				words.add(token);
			}
		}
		return (String[]) words.toArray();
	}
}
