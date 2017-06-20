package com.fors.ir.model;

import java.util.HashMap;
import java.util.List;

public class Index {

	private HashMap<Integer, Document> docs;
	private HashMap<String, Term> terms;
	private HashMap<String, String> stopWords;
	Stemmer stemmer;
	
	public Index(HashMap<Integer, Document> docs2, HashMap<String, String> stopWords){
		this.docs = docs2;
		this.stopWords = stopWords;
		terms = new HashMap<String, Term>();
		stemmer = new Stemmer();
		this.createTermSet();
		this.computeDocumentVectors();
	}
	
	private void createTermSet(){
		for (Document doc : docs.values()){
			List<String> termBag = doc.getDocTermBag();
			for (String term : termBag) {
				term = term.toLowerCase();
				this.addTerm(term, doc);
			}
		}		
	}
	
	private void computeDocumentVectors(){
		// Compute document vectors
		for (Term term : this.getTerms().values()) {
			for (Posting posting : term.getPostings().values()) {
				Document doc = this.getDoc(posting.docId);
				doc.setLength(doc.getLength() + Math.pow(posting.tf_idf(), 2));
			}
		}		
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
	
	public Term getTerm(String term){
		if (termExists(term))
			return (Term) terms.get(term);
		else
			return null;
	}
		
	private void addTerm(String term, Document doc) {
		if (isStopWord(term))
			return;
		
		//Stem the term using Porter's Stemming algorithm
		char[] termChars = term.toCharArray();
		stemmer.add(termChars, term.length());
		stemmer.stem();
		term = stemmer.toString();
		
		Term termCls;
		if (!termExists(term)) {
			termCls = new Term(term, docs.size());
			terms.put(term, termCls);
		}
		else
			termCls = getTerm(term);
		
		Posting posting;
		if (termCls.postingExists(doc.getDocId()))
			posting = termCls.getPosting(doc.getDocId());
		else {
			posting = termCls.addPosting(doc);
		}
		posting.frequency++;
	}
	
	public HashMap<String, Term> getTerms(){
		return terms;
	}

	public int N() {
		return docs.size();
	}
	
	public Document getDoc(String docId){
		Document doc = docs.get(docId);
		return doc;
	}
	
	public HashMap<Integer, Document> getDocs(){
		return this.docs;
	}
	
	public HashMap<String, String> getStopwords(){
		return this.stopWords;
	}
}
