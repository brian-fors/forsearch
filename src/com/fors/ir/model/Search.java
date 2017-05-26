package com.fors.ir.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.fors.ir.controller.Main;

public class Search {

	private HashMap<Integer, DocumentMatch> docHits;
	
	public LinkedHashMap<Integer, Double> Execute(Index index, String queryString){

		// Create HashMap vector for the query
		Query query = new Query(index.getStopwords());

		List<String> queryBag = new ArrayList<String>(Arrays.asList(queryString.split("[ .,?!()']+")));
		for (String term : queryBag) {
			term = term.toLowerCase();
			query.addTerm(term);
		}
		
		// Print query terms for debug
		if (Main.DEBUG_MODE) {
			for (QueryTerm queryTerm : query.getQueryTerms().values()) {
				System.out.println(queryTerm.term);
			}
		}
		
		// Create HashMap to store retrieved documents
		docHits = new HashMap<Integer, DocumentMatch>();
		
		// Iterate through tokens in Q
		for (QueryTerm queryTerm : query.getQueryTerms().values()) {
			if (index.termExists(queryTerm.term)) {
				Term term = index.getTerm(queryTerm.term);
				queryTerm.tf_idf = queryTerm.frequency * term.idf_k();
				for (Posting posting : term.getPostings().values()){
					DocumentMatch docMatch;
					if (docHits.containsKey(posting.docId))
						docMatch = docHits.get(posting.docId);
					else {
						docMatch = new DocumentMatch(posting.docId);
						docHits.put(posting.docId, docMatch);
					}
					docMatch.score += queryTerm.tf_idf * posting.tf_idf(); 
				}
			}
		}
		HashMap<Integer, Double> tempResults = new HashMap<Integer, Double>(); 
		for (DocumentMatch docMatch : docHits.values()) {
			Document doc = index.getDocs().get(docMatch.docId);
			docMatch.cosSim = docMatch.score / (query.getLength() * doc.getLength());
			tempResults.put(docMatch.docId, docMatch.cosSim);
			// System.out.println(docMatch.docId + "-" + docMatch.score + "-" + docMatch.cosSim);
		}
		
		// Sort the results
		LinkedHashMap<Integer, Double> sortedResults = sortHashMapByValues(tempResults);
		
		return sortedResults;
	}
	
	public DocumentMatch getDocHit(int docId) {
		return docHits.get(docId);
	}
	
	public static LinkedHashMap<Integer, Double> sortHashMapByValues(HashMap<Integer, Double> passedMap) {
	    List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
	    List<Double> mapValues = new ArrayList<Double>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	        
	    LinkedHashMap<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
	    
	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Object val = valueIt.next();
	        Iterator<Integer> keyIt = mapKeys.iterator();
	        
	        while (keyIt.hasNext()) {
	            Object key = keyIt.next();
	            String comp1 = passedMap.get(key).toString();
	            String comp2 = val.toString();
	            
	            if (comp1.equals(comp2)){
	                passedMap.remove(key);
	                mapKeys.remove(key);
	                sortedMap.put((Integer)key, (Double)val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}		
	
	public static List<Integer> sortHashMapByKeys(HashMap<Integer, Double> passedMap) {
	    List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
	    Collections.sort(mapKeys);
	        
	    return mapKeys;
	}		

}
