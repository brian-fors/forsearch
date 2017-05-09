package com.fors.ir.model;

public class QueryTerm {
	public String term;
	public int frequency;
	public double tf_idf;
	
	public QueryTerm(String term){
		this.term = term;
	}
}
