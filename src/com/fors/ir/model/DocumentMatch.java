package com.fors.ir.model;

public class DocumentMatch {
	public String docId;
	public double score;
	public double cosSim;
	
	public DocumentMatch(String docId2) {
		this.docId = docId2;
	}
}
