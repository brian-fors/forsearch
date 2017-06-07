package com.fors.ir.controller;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.elasticsearch.action.search.SearchResponse;

import com.fors.ir.model.Document;
import com.fors.ir.model.ElasticIndex;
import com.fors.ir.model.Index;
import com.fors.ir.model.Posting;
import com.fors.ir.model.Search;
import com.fors.ir.model.Term;
import com.fors.ir.view.ClientView;

public class Main {

	public static final Boolean REBUILD_INDEX = true;

	public enum DocSet {TIME, MEDLARS, CRANFIELD, PATDEMO};
	public enum IndexTypes {TF_IDF, ELASTIC};
	
	public static double cosSimThreshold = 1.75;
	public static float maxScoreThreshold = 50;
	
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

		IndexTypes indexType = IndexTypes.ELASTIC;
		
		if (args.length > 0 && args[0].contains("--debug"))
			DEBUG_MODE = true;

		System.out.println("Loading documents...");
		docs = client.getPatDemo("C:\\Git\\forsearch\\data\\patdemo\\patdemo.csv");
		if (DEBUG_MODE) {
			 //Display documents for debugging
			System.out.println();
			for (Document doc : docs.values()){
				System.out.println(doc.getDocId() + "-" + doc.getLength());
			}
		}
		System.out.println(docs.size() + " documents loaded.");

		HashMap<String, String> stopwords =
			client.getStopwords("data\\stopwords\\stopwords.txt");

		Index index;
		ElasticIndex elasticIndex;
		if (indexType == IndexTypes.TF_IDF) {
			System.out.println("Indexing documents...");
			index = new Index(docs, stopwords);
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
		    System.out.println("============================");
		    System.out.println("        cosSim > " + Double.toString(cosSimThreshold) + "       ");
		    System.out.println("============================");
		    
		    System.out.println("Finding document matches...");
			for (Document doc: docs.values()) {
				Search search = new Search();
				LinkedHashMap<Integer, Double> results = search.Execute(index, doc.toString());
				
				// Filter matches meeting cosSim threshold
				client.filterResults(doc, doc.toString(), results, index, search, cosSimThreshold);

				// Display matches
				client.displayDocMatches(doc, docs);
			}
		
		} else if (indexType == IndexTypes.ELASTIC) {
			elasticIndex = new ElasticIndex();
			System.out.println("Creating index...");
			elasticIndex.create();
			if (REBUILD_INDEX) {
				Thread.sleep(2000);
				System.out.println("Indexing documents...");
//				elasticIndex.indexDocuments(docs);
				elasticIndex.bulkIndexDocuments(docs);
			}
		    System.out.println("============================");
		    System.out.println("        maxScore > " + Double.toString(maxScoreThreshold) + "       ");
		    System.out.println("============================");

		    System.out.println("Finding document matches...");
			BufferedWriter bw = null;
			FileWriter fw = null;
			String FILENAME = "C:\\Git\\forsearch\\data\\patdemo\\patdemo.out";
			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);

			for (Document doc: docs.values()) {
//				System.out.print(".");
				SearchResponse response = elasticIndex.search(doc);
				if (response.getHits().getTotalHits() > 1) {
//					System.out.println(doc.toString());
//					client.displayElasticResponse(doc, response);
					client.writeElasticResponse(bw, doc, response);
				}
			}
			bw.close();
			fw.close();
		}
	}
}
