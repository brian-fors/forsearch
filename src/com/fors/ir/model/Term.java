package com.fors.ir.model;

import java.util.HashMap;

public class Term {

	private String term;
	public int N;
	public HashMap<Integer, Posting> postings;
	
	public Term(String term, int N){
		this.term = term;
		this.N = N;
		postings = new HashMap<Integer, Posting>();
	}
	
	public Posting addPosting(Document doc){
		Posting posting = new Posting(doc.getDocId(), this);
		postings.put(doc.getDocId(), posting);
		return posting;
	}
	
	public boolean postingExists(int docId) {
		return postings.containsKey(docId);
	}
	
	public Posting getPosting(int docId) {
		return postings.get(docId);
	}
	
	public String getTerm(){
		return term;
	}
	
	public HashMap<Integer, Posting> getPostings(){
		return postings;
	}
	
	public int n_k() {
		return postings.size();
	}
	
	public double idf_k() {
		double val = N / n_k();
		val = Math.log(val);
		return val;
	}
}
