package com.fors.ir.controller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import com.fors.ir.model.Document;
import com.fors.ir.model.DocumentMatch;
import com.fors.ir.model.Index;
import com.fors.ir.model.Posting;
import com.fors.ir.model.Search;
import com.fors.ir.model.Term;
import com.fors.ir.view.ClientView;

public class Main {

	public enum DocSet {TIME, MEDLARS, CRANFIELD};

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ClientView client = new ClientView();
		HashMap<Integer, Document> docs = null;
		DocSet docSet = client.getDocSet();

		switch (docSet) {
		case TIME :
			docs = client.getDocs("data\\time\\doc.text");
			break;
		case MEDLARS :
			docs = client.getMedlarsDocs("data\\medlars\\med.all");
			break;
		case CRANFIELD :
			docs = client.getCranfieldDocs("data\\cranfield\\cran.all");
			break;
		}

		HashMap<String, String> stopwords =
			client.getStopwords("data\\stopwords\\stopwords.txt");

		// Create index
		Index index = new Index(docs, stopwords);

		// Print index for debug
		// for (Term term : index.getTerms().values()) {
			// System.out.print(term.getTerm() + "-" + term.postings.size() + ",");
			// for (@SuppressWarnings("unused") Posting posting : term.getPostings().values()) {
				// System.out.print(posting.docId + "-" + posting.frequency + "-" + posting.tf_idf() + ";");
			// }
			// System.out.println();
		// }

		System.out.println(index.getTerms().size() + " terms loaded.");
		// System.out.println();
		// for (@SuppressWarnings("unused") Document doc : docs.values()){
			// System.out.println(doc.getDocId() + "-" + doc.getLength());
		// }
		System.out.println(docs.size() + " documents loaded.");

		Search search = new Search();
		while (true){
			String queryString = client.getString("Enter query:");
			if (queryString.equals("Q")) {
				// Quit
				System.out.println("End.");
				return;
			}
			else if (queryString.startsWith("*D")) {
				// Display document text
				int docId = Integer.parseInt(queryString.substring(2));
				Document doc = index.getDoc(docId);
				System.out.print(doc.document);
				System.out.println();
			}
			else {
				// Display search results
				System.out.println("Query: " + queryString);
				LinkedHashMap<Integer, Double> sortedResults = search.Execute(index, queryString);
			    List<Integer> c = new ArrayList<Integer>(sortedResults.keySet());

			    System.out.println("============================");
			    System.out.println("           TOP 50           ");
			    System.out.println("============================");

			    // List of keys only
			    List<Integer> keys = new ArrayList<Integer>();

			    ListIterator<Integer> listItr = c.listIterator(c.size());
			    int i = 0;
			    while(listItr.hasPrevious() && i<50){
			    	int docId = (Integer)listItr.previous();
			    	keys.add(docId);
			    	Document doc = index.getDoc(docId);
			    	DocumentMatch docMatch = search.getDocHit(docId);
			    	double docCosSim = (double)Math.round(docMatch.cosSim * 1000) / 1000;
			    	System.out.println(i+1 + "-" + docId + "-" + docCosSim + "-" + doc.document.substring(0, 80));
			    	i++;
			    }

				System.out.println();
			    // System.out.println("*** SORTED Document IDs ***");
			    // Collections.sort(keys);
				// for (Integer key : keys) System.out.println(key);
			    // System.out.println("*** END SORTED Document IDs ***");
			}
		}
	}
}
