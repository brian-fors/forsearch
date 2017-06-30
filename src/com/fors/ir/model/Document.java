package com.fors.ir.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.language.Soundex;

import com.fors.ir.controller.Main;

import org.apache.commons.codec.language.Caverphone1;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.Nysiis;

public class Document {
	private String docId;
	public String document;
	private HashMap<String, DocumentMatch> docMatches;
	private double length;
	private List<String> docTermBag = new ArrayList<String>();

	private String enterpriseId;
	private String lastName;
	private String firstName;
	private String middleName;
	private String suffix;
	private String birthDate;
	private String gender;
	private String ssn;
	private String address1;
	private String address2;
	private String zip;
	private String motherMaidenName;
	private String mrn;
	private String city;
	private String state;
	private String phone1;
	private String phone2;
	private String email;
	private String alias;
	
	private String zipChecksum;
	private String birthDateChecksum;
	
	public Document(String docId) {
		this.setDocId(docId);
		this.document = "";
		this.docMatches = new HashMap<String, DocumentMatch>();
	}
	public void addText(String text) {
		this.document += text;
	}
	public void parse() {
		docTermBag = new ArrayList<String>(Arrays.asList(document.split(",", -1)));

		enterpriseId = docTermBag.get(0);
		lastName = docTermBag.get(1);
		firstName = docTermBag.get(2);
		middleName = docTermBag.get(3);
		suffix = docTermBag.get(4);
		birthDate = docTermBag.get(5);
		gender = docTermBag.get(6);
		ssn = docTermBag.get(7);
		address1 = docTermBag.get(8);
		address2 = docTermBag.get(9);
		zip = docTermBag.get(10);
		motherMaidenName = docTermBag.get(11);
		mrn = docTermBag.get(12);
		city = docTermBag.get(13);
		state = docTermBag.get(14);
		phone1 = docTermBag.get(15);
		phone2 = docTermBag.get(16);
		email = docTermBag.get(17);
		alias = docTermBag.get(18);
		
		this.setDocId(enterpriseId);
		for (int i = 1; i < Main.ZIPCODE_WEIGHT_FACTOR; i++) {
			docTermBag.add(zip);
		}
		// Repeat birthDate in term bag so it gets more index weight
		for (int i = 1; i < Main.BIRTHDATE_WEIGHT_FACTOR; i++) {
			docTermBag.add(birthDate);
		}

		if (docTermBag.size() == 19) {
			alias = docTermBag.get(18);
		}
		
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
			docTermBag.add(Soundex.US_ENGLISH.encode(alias));
		}
		
		//Add Nysiis terms for name attributes
		if (Main.ENABLE_NYIIS) {
			Nysiis nysiis = new Nysiis();
			docTermBag.add(nysiis.encode(firstName));
			docTermBag.add(nysiis.encode(middleName));
			docTermBag.add(nysiis.encode(lastName));
			docTermBag.add(nysiis.encode(alias));
		}
		
		if (Main.ENABLE_CAVERPHONE1) {
			Caverphone1 cv1 = new Caverphone1();
			docTermBag.add(cv1.encode(firstName));
			docTermBag.add(cv1.encode(middleName));
			docTermBag.add(cv1.encode(lastName));
			docTermBag.add(cv1.encode(alias));
		}
		
		if (Main.ENABLE_CAVERPHONE2) {
			Caverphone2 cv2 = new Caverphone2();
			docTermBag.add(cv2.encode(firstName));
			docTermBag.add(cv2.encode(middleName));
			docTermBag.add(cv2.encode(lastName));
			docTermBag.add(cv2.encode(alias));
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
	public Map<String, Object> toJson() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("lastName", lastName);
		json.put("firstName", firstName);
		json.put("middleName", middleName);
		json.put("suffix", suffix);
		json.put("birthDate", birthDate);
		json.put("gender", gender);
		json.put("ssn", ssn);
		json.put("address1", address1);
		json.put("address2", address2);
		json.put("zip", zip);
		json.put("motherMaidenName", motherMaidenName);
		json.put("mrn", mrn);
		json.put("city", city);
		json.put("state", state);
		json.put("phone1", phone1);
		json.put("phone2", phone2);
		json.put("email", email);
		json.put("alias", alias);
		
		if (Main.ENABLE_DIGITCHECKSUM) {
			json.put("zipChecksum", zipChecksum);
			json.put("birthDateChecksum", birthDateChecksum);
		};
		if (Main.ENABLE_SOUNDEX) {
			json.put("firstNameSoundex", Soundex.US_ENGLISH.encode(firstName));
			json.put("middleNameSoundex", Soundex.US_ENGLISH.encode(middleName));
			json.put("lastNameSoundex", Soundex.US_ENGLISH.encode(lastName));
		}
		if (Main.ENABLE_NYIIS) {
			Nysiis nysiis = new Nysiis();
			json.put("firstNameNysiis", nysiis.encode(firstName));
			json.put("middleNameNysiis", nysiis.encode(middleName));
			json.put("lastNameNysiis", nysiis.encode(lastName));
		}
		if (Main.ENABLE_CAVERPHONE1) {
			Caverphone1 cv1 = new Caverphone1();
			json.put("firstNameCV1", cv1.encode(firstName));
			json.put("middleNameCV1", cv1.encode(middleName));
			json.put("lastNameCV1", cv1.encode(lastName));
		}
		if (Main.ENABLE_CAVERPHONE2) {
			Caverphone2 cv2 = new Caverphone2();
			json.put("firstNameCV2", cv2.encode(firstName));
			json.put("middleNameCV2", cv2.encode(middleName));
			json.put("lastNameCV2", cv2.encode(lastName));
		}
		
		return json;

	}
	
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getDocId() {
		return docId;
	}
	public void addDocMatch(DocumentMatch docMatch) {
		this.docMatches.put(docMatch.docId, docMatch);
	}
	public HashMap<String, DocumentMatch> getDocMatches() {
		return this.docMatches;
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
