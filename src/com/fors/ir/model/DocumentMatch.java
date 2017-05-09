package com.fors.ir.model;

public class DocumentMatch {
	public int docId;
	public double score;
	public double cosSim;
	
	public DocumentMatch(int docId) {
		this.docId = docId;
	}
}
