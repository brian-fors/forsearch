package com.fors.ir.controller;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.fors.ir.model.Document;
import com.fors.ir.model.Index;
import com.fors.ir.model.Search;
import com.fors.ir.view.ClientView;

public class Main {

	public enum DocSet {TIME, MEDLARS, CRANFIELD, PATDEMO};

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
		case PATDEMO :
			docs = client.getPatDemo("data\\patdemo\\patdemo.csv");
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
			String query = client.getString("Enter query:");
			if (query.equals("Q")) {
				// Quit
				System.out.println("End.");
				return;
			}
			else if (query.startsWith("*D")) {
				client.displayDocumentText(query, index);
			}
			else {
				LinkedHashMap<Integer, Double> results = search.Execute(index, query);
		    	client.displayResults(query, results, index, search);
			}
		}
	}
}
