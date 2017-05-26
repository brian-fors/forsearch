package com.fors.ir.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.language.Soundex;

import com.fors.ir.controller.Main;

import org.apache.commons.codec.language.Caverphone1;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.Nysiis;

public class Document {
	private int docId;
	public String document;
	private double length;
	private List<String> docTermBag = new ArrayList<String>();

//	private String state;
	private String zip;
	private String birthDate;
//	private String gender;
	private String firstName;
	private String middleName;
	private String lastName;
	
	private String zipChecksum;
	private String birthDateChecksum;
	
	public Document(int docId) {
		this.setDocId(docId);
		this.document = "";
	}
	public void addText(String text) {
		this.document += text;
	}
	public void parse() {
		docTermBag = new ArrayList<String>(Arrays.asList(document.split("[ .,?!()']+")));
//		state = docTermBag.get(0);
		zip = docTermBag.get(1);
		for (int i = 1; i < Main.ZIPCODE_WEIGHT_FACTOR; i++) {
			docTermBag.add(zip);
		}
//		gender = docTermBag.get(2);
		birthDate = docTermBag.get(3);
		// Repeat birthDate in term bag so it gets more index weight
		for (int i = 1; i < Main.BIRTHDATE_WEIGHT_FACTOR; i++) {
			docTermBag.add(birthDate);
		}
		firstName = docTermBag.get(4);
		middleName = docTermBag.get(5);
		lastName = docTermBag.get(6);
		
		//Add checksum terms for numeric attributes
		if (Main.ENABLE_DIGITCHECKSUM) {
			zipChecksum = checksum(zip);
			for (int i = 1; i < Main.ZIPCODECHECKSUM_WEIGHT_FACTOR; i++) {
				docTermBag.add(zipChecksum);
			}
			birthDateChecksum = checksum(birthDate);
			for (int i = 1; i < Main.BIRTHDATECHECKSUM_WEIGHT_FACTOR; i++) {
				docTermBag.add(birthDateChecksum);
			}
		}
		
		//Add Soundex terms for name attributes
		if (Main.ENABLE_SOUNDEX) {
			docTermBag.add(Soundex.US_ENGLISH.encode(firstName));
			docTermBag.add(Soundex.US_ENGLISH.encode(middleName));
			docTermBag.add(Soundex.US_ENGLISH.encode(lastName));
		}
		
		//Add Nysiis terms for name attributes
		if (Main.ENABLE_NYIIS) {
			Nysiis nysiis = new Nysiis();
			docTermBag.add(nysiis.encode(firstName));
			docTermBag.add(nysiis.encode(middleName));
			docTermBag.add(nysiis.encode(lastName));
		}
		
		if (Main.ENABLE_CAVERPHONE1) {
			Caverphone1 cv1 = new Caverphone1();
			docTermBag.add(cv1.encode(firstName));
			docTermBag.add(cv1.encode(middleName));
			docTermBag.add(cv1.encode(lastName));
		}
		
		if (Main.ENABLE_CAVERPHONE2) {
			Caverphone2 cv2 = new Caverphone2();
			docTermBag.add(cv2.encode(firstName));
			docTermBag.add(cv2.encode(middleName));
			docTermBag.add(cv2.encode(lastName));
		}
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getLength() {
		double val = Math.sqrt(length);
		val = (double)Math.round(val * 10000) / 10000;
		return val;
	}
	public List<String> getDocTermBag() {
		return docTermBag;
	}
	public String toString() {
		String docString = String.join(", ", docTermBag);
		return docString;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getDocId() {
		return docId;
	}
	
	private String checksum(String string) {
		string = string.replace("-", "");
		string = string.replace("/", "");
		int sum = 0;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			sum += Character.getNumericValue(c);
		}
		return Integer.toString(sum);
	}
	
}
