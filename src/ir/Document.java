package ir;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Represents one document, or "quote" in this case.
 * Keeps track of the term frequencies.
 */
public class Document implements Comparable<Document> {
	
	/**
	 * Maps a term to the number of times this terms appears in this document. 
	 */
	private Map<String, Integer> termFrequency;
	
	/**
	 * The text stored.
	 */
	private String text;
	
	/**
	 * Takes in the the text that the document should store and pre-processes it.
	 * 
	 * @param text the text to process
	 */
	public Document(String text) {
		this.text = text.toLowerCase();
		termFrequency = new HashMap<String, Integer>();
		
		readFileAndPreProcess();
	}
	
	/**
	 * Reads in the file and does pre-processing.
	 * The following things are done in pre-processing:
	 * Every word is converted to lower case.
	 * Every character that is not a letter or a digit is removed.
	 * We don't do any stemming.
	 * Once the pre-processing is done, we create and update the term frequency map.
	 */
	private void readFileAndPreProcess() {
		Scanner in = new Scanner(text);
		
		while (in.hasNext()) {
			String nextWord = in.next();
			
			String filteredWord = nextWord.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			
			if (!(filteredWord.equalsIgnoreCase(""))) {
				if (termFrequency.containsKey(filteredWord)) {
					int oldCount = termFrequency.get(filteredWord);
					termFrequency.put(filteredWord, ++oldCount);
				} else {
					termFrequency.put(filteredWord, 1);
				}
			}
		}
		in.close();
	}
	
	/**
	 * Returns the term frequency for a given word.
	 * If this document doesn't contain the word, returns 0.
	 * 
	 * @param word The word to look for
	 * @return the term frequency for this word in this document
	 */
	public double getTermFrequency(String word) {
		if (termFrequency.containsKey(word)) {
			return termFrequency.get(word);
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns a set of all the terms which occur in this document.
	 * 
	 * @return a set of all terms in this document
	 */
	public Set<String> getTermList() {
		return termFrequency.keySet();
	}

	@Override
	public int compareTo(Document other) {
		return text.compareTo(other.getText());
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return text;
	}

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Document other = (Document) obj;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }
}