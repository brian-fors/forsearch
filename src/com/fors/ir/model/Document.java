package com.fors.ir.model;

public class Document {
	private int docId;
	public String document;
	private double length;
	private String[] docTermBag;
	
	public Document(int docId) {
		this.setDocId(docId);
		this.document = "";
	}
	public void addText(String text) {
		this.document += text;
	}
	public void parse() {
		docTermBag = document.split("[ .,?!()']+");
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getLength() {
		double val = Math.sqrt(length);
		val = (double)Math.round(val * 10000) / 10000;
		return val;
	}
	public String[] getDocTermBag() {
		return docTermBag;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getDocId() {
		return docId;
	}
	
}
