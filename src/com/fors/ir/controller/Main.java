package com.fors.ir.controller;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.fors.ir.model.Document;
import com.fors.ir.model.Index;
import com.fors.ir.model.Posting;
import com.fors.ir.model.Search;
import com.fors.ir.model.Term;
import com.fors.ir.view.ClientView;

public class Main {

	public enum DocSet {TIME, MEDLARS, CRANFIELD, PATDEMO};
	public static double cosSimThreshold = 1.75;
	public static boolean ENABLE_NYIIS = false;
	public static boolean ENABLE_SOUNDEX = true;
	public static boolean ENABLE_CAVERPHONE1 = true;
	public static boolean ENABLE_CAVERPHONE2 = false;
	public static boolean ENABLE_DIGITCHECKSUM = true;
	public static boolean DEBUG_MODE = false;
	public static int BIRTHDATE_WEIGHT_FACTOR = 4;
	public static int BIRTHDATECHECKSUM_WEIGHT_FACTOR = 4;
	public static int ZIPCODE_WEIGHT_FACTOR = 1;
	public static int ZIPCODECHECKSUM_WEIGHT_FACTOR = 5;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ClientView client = new ClientView();
		HashMap<Integer, Document> docs = null;
		
		DocSet docSet = client.getDocSet(DocSet.PATDEMO);
		if (args.length > 0 && args[0].contains("--debug"))
			DEBUG_MODE = true;

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

		if (DEBUG_MODE) {
			 //Display index for debugging
			 for (Term term : index.getTerms().values()) {
				 System.out.print(term.getTerm() + "-" + term.postings.size() + ",");
				 for (Posting posting : term.getPostings().values()) {
					 System.out.print(posting.docId + "-" + posting.frequency + "-" + posting.tf_idf() + ";");
				 }
				 System.out.println();
			 }
		}

		System.out.println(index.getTerms().size() + " terms loaded.");
		if (DEBUG_MODE) {
			 //Display documents for debugging
			System.out.println();
		
			for (Document doc : docs.values()){
				System.out.println(doc.getDocId() + "-" + doc.getLength());
			}
		}
		System.out.println(docs.size() + " documents loaded.");
		
		if (docSet == DocSet.PATDEMO) {
		    System.out.println("============================");
		    System.out.println("        cosSim > " + Double.toString(cosSimThreshold) + "       ");
		    System.out.println("============================");
			// Iterate through each document and find matches
			for (Document doc: docs.values()) {
				Search search = new Search();
				LinkedHashMap<Integer, Double> results = search.Execute(index, doc.toString());
				
				// Filter matches meeting cosSim threshold
				client.filterResults(doc, doc.toString(), results, index, search, cosSimThreshold);

				// Display matches
				client.displayDocMatches(doc, docs);
			}
		}
		else {
			client.processUserQuery(index);
		}
	}
}
