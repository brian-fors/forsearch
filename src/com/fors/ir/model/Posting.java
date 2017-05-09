package com.fors.ir.model;

public class Posting {

	public int docId;
	public int frequency;
	private Term term;
	
	public Posting(int docId, Term term){
		this.docId = docId;
		this.frequency = 0;
		this.term = term;
	}
	
	public double tf_idf() {
		double val = frequency * term.idf_k();
		val = (double)Math.round(val * 10000) / 10000;
		return val;
	}	
}
