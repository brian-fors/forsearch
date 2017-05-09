package com.fors.ir.model;
import java.util.HashMap;

public class Query {
	private HashMap<String, QueryTerm> terms;
	private HashMap<String, String> stopWords;
	Stemmer stemmer;
	
	public Query(HashMap<String, String> stopWords){
		this.stopWords = stopWords;
		terms = new HashMap<String, QueryTerm>();
		stemmer = new Stemmer();
	}
	
	public boolean termExists(String term) {
		if (terms.containsKey(term))
			return true;
		else
			return false;
	}
	
	private boolean isStopWord(String term) {
		if (stopWords.containsKey(term))
			return true;
		else
			return false;
	}
	
	public QueryTerm getTerm(String term){
		if (termExists(term))
			return (QueryTerm) terms.get(term);
		else
			return null;
	}
		
	public void addTerm(String term) {
		if (isStopWord(term))
			return;
		
		//Stem the term using Porter's Stemming algorithm
		char[] termChars = term.toCharArray();
		stemmer.add(termChars, term.length());
		stemmer.stem();
		term = stemmer.toString();
		
		QueryTerm queryTerm;
		if (!termExists(term)) {
			queryTerm = new QueryTerm(term);
			terms.put(term, queryTerm);
		}
		else
			queryTerm = getTerm(term);
		queryTerm.frequency++;
	}
	
	public HashMap<String, QueryTerm> getQueryTerms(){
		return terms;
	}

	public double getLength() {
		double val = 0.0;
		for (QueryTerm term: terms.values()) {
			val += Math.pow(term.tf_idf, 2);
		}
		val = Math.sqrt(val);
		return val;
	}
}
