package ir;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a corpus of documents.
 * Creates an inverted index for these documents.
 */
public class Corpus {
	
	/**
	 * A list of all documents in the corpus.
	 */
	private List<Document> documents;
	
	/**
	 * The inverted index. 
	 * Maps a term to a set of documents that contain that term.
	 */
	private Map<String, Set<Document>> invertedIndex;
	
	/**
	 * Generates the inverted index based on the documents.
	 * 
	 * @param documents the list of documents
	 */
	public Corpus(List<Document> documents) {
		this.documents = documents;
		invertedIndex = new HashMap<String, Set<Document>>();
		
		createInvertedIndex();
	}
	
	/**
	 * Creates an inverted index.
	 */
	private void createInvertedIndex() {
		for (Document document : documents) {
			Set<String> terms = document.getTermList();
			
			for (String term : terms) {
				if (invertedIndex.containsKey(term)) {
					Set<Document> list = invertedIndex.get(term);
					list.add(document);
				} else {
					Set<Document> list = new TreeSet<Document>();
					list.add(document);
					invertedIndex.put(term, list);
				}
			}
		}
	}
	
	/**
	 * Returns the idf for a given term.
	 * 
	 * @param term a term in a document
	 * @return the idf for the term
	 */
	public double getInverseDocumentFrequency(String term) {
		if (invertedIndex.containsKey(term)) {
			double size = documents.size();
			Set<Document> list = invertedIndex.get(term);
			double documentFrequency = list.size();
			
			return Math.log10(size / documentFrequency);
		} else {
			return 0;
		}
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @return the invertedIndex
	 */
	public Map<String, Set<Document>> getInvertedIndex() {
		return invertedIndex;
	}
}