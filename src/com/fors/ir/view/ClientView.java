package com.fors.ir.view;
/**
 *  ClientView contains methods to get parameters from the user.
 *
 *  @author     Brian Fors
 *  @version    1.0
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import com.fors.ir.controller.Main;
import com.fors.ir.controller.Main.DocSet;
import com.fors.ir.model.Document;
import com.fors.ir.model.DocumentMatch;
import com.fors.ir.model.Index;
import com.fors.ir.model.Search;

public class ClientView {

	public DocSet getDocSet(DocSet defaultVal) throws IOException {
		
		if (defaultVal != null) {
			System.out.println("  Defaulting to " + defaultVal);
			return defaultVal;
		}
		
		System.out.println("  Which document set to index (0=TIME, 1=MEDLARS, 2=CRANFIELD, 3=PATDEMO):");
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		int i = Integer.parseInt(in.readLine());
		switch (i) {
		case 0:
			return DocSet.TIME;
		case 1:
			return DocSet.MEDLARS;
		case 2:
			return DocSet.CRANFIELD;
		case 3:
			return DocSet.PATDEMO;
		}
		throw new IOException("Unable to determine which document set to process.");
	}
	
	public void processUserQuery(Index index) throws IOException {
		Search search = new Search();
		while (true){
			String query = getString("Enter query:");
			if (query.equals("Q")) {
				System.out.println("End.");
				return;
			}
			else if (query.startsWith("*D")) {
				displayDocumentText(query, index);
			}
			else {
				LinkedHashMap<String, Double> results = search.Execute(index, query);
		    	displayResults(query, results, index, search);
			}
		}		
	}

	public String getFileName() throws IOException {
		System.out.println("  Enter data file name:");
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return in.readLine();
	}

	public String getString(String prompt) throws IOException {
		System.out.println(prompt);
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return in.readLine();
	}

	public HashMap<Integer, Document> getPatDemo(String filename) throws FileNotFoundException, IOException {
		String line;
		int docId = 0;
		if (filename == null)
			filename = this.getFileName();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		HashMap<Integer, Document> docs = new HashMap<Integer, Document>();
		Document doc;
		while ((line = reader.readLine()) != null) {
				docId++;
				doc = new Document(Integer.toString(docId));
				doc.addText(line);
				docs.put(docId, doc);
		}
		for (Document doc2 : docs.values()) {
			doc2.parse();
		}
		reader.close();
		return docs;
	}

	public HashMap<String, String> getStopwords(String filename) throws FileNotFoundException, IOException {
		String stopword;
		if (filename == null)
			filename = this.getFileName();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		HashMap<String, String> stopwords = new HashMap<String, String>();
		while ((stopword = reader.readLine()) != null) {
			if (!stopword.equals(""))
				stopwords.put(stopword.toLowerCase(), stopword.toLowerCase());
		}
		reader.close();
		return stopwords;
	}

	public void displayResults(String query, LinkedHashMap<String, Double> results, Index index, Search search){

		System.out.println("Query: " + query);
	    if (results.size() == 0) {
	    	System.out.println("Sorry, no matching results found.");
	    	return;
	    }

		List<String> c = new ArrayList<String>(results.keySet());
	    System.out.println("============================");
	    System.out.println("           TOP 50           ");
	    System.out.println("============================");

	    // List of keys only
	    List<String> keys = new ArrayList<String>();

	    ListIterator<String> listItr = c.listIterator(c.size());
	    int i = 0;
	    while(listItr.hasPrevious() && i<50){
	    	String docId = listItr.previous();
	    	keys.add(docId);
	    	Document doc = index.getDoc(docId);
	    	DocumentMatch docMatch = search.getDocHit(docId);
	    	double docCosSim = (double)Math.round(docMatch.cosSim * 1000) / 1000;
	    	int displayLength = (int) Math.min(80, doc.document.length());
	    	System.out.println(i+1 + "-" + docId + "-" + docCosSim + "-" + doc.document.substring(0, displayLength));
	    	i++;
	    }

		System.out.println();
	};	
	
	public int getInt(String userPrompt) throws IOException {
		System.out.println(userPrompt);
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return Integer.parseInt(in.readLine());
	}

	public double getDouble(String userPrompt) throws IOException {
		System.out.println(userPrompt);
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return Double.valueOf(in.readLine());
	}
	
	
	public void filterResults(Document sourceDoc, String query, LinkedHashMap<String, Double> results, Index index, Search search, double cosSimThreshold){

		if (Main.DEBUG_MODE == true) {
			System.out.println("Query: " + query);
		}
	    if (results.size() == 0) {
	    	System.out.println("Sorry, no matching results found.");
	    	return;
	    }

		List<String> c = new ArrayList<String>(results.keySet());

	    ListIterator<String> listItr = c.listIterator(c.size());
	    while(listItr.hasPrevious()){
	    	String docId = listItr.previous();
	    	DocumentMatch docMatch = search.getDocHit(docId);
	    	double docCosSim = (double)Math.round(docMatch.cosSim * 1000) / 1000;
	    	if (docId != sourceDoc.getDocId() && docCosSim > cosSimThreshold) {
	    		sourceDoc.addDocMatch(docMatch);
	    	}
	    }
	};
	
	public void displayDocMatches(Document doc, HashMap<Integer, Document> docSet) {
		int maxMatches = 10;
		int i = 0; 
		if (doc.getDocMatches().size() > 0) {
			for (DocumentMatch docMatch: doc.getDocMatches().values()) {
				i++; if (i > maxMatches) return;
				System.out.print(doc.getDocId() + ",");
				System.out.print(docMatch.docId + "," + docMatch.cosSim);
				System.out.println();
//				System.out.println(doc.getDocId() + "-" + doc.toString());
//				System.out.println(docMatch.docId + "-" + docSet.get(docMatch.docId).toString());
			} 
		}
	}
	
	public void displayElasticResponse(Document doc, SearchResponse response) {
		for (SearchHit hit: response.getHits()) {
			if (!doc.getDocId().equals(hit.getId())) {
				System.out.println(doc.getDocId() + "," + hit.getId() + "," + hit.getScore());
			} 
		}
	}
	
	public void writeElasticResponse(BufferedWriter writer, Document doc, SearchResponse response) {
		try {
			for (SearchHit hit: response.getHits()) {
				if (!doc.getDocId().equals(hit.getId())) {
					writer.write(doc.getDocId() + "," + hit.getId() + "," + hit.getScore() + "\n");
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void displayDocumentText(String query, Index index){
		// Display document text
		String docId = query.substring(2);
		Document doc = index.getDoc(docId);
		System.out.print(doc.document);
		System.out.println();
	}
}
