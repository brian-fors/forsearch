package com.fors.ir.view;
/**
 *  ClientView contains methods to get parameters from the user.
 *
 *  @author     Brian Fors
 *  @version    1.0
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.fors.ir.controller.Main.DocSet;
import com.fors.ir.model.Document;

public class ClientView {

	public DocSet getDocSet() throws IOException {
		System.out.println("  Which document set to index (0=TIME, 1=MEDLARS, 2=CRANFIELD):");
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		int i = Integer.parseInt(in.readLine());
		switch (i) {
		case 0:
			return DocSet.TIME;
		case 1:
			return DocSet.MEDLARS;
		case 2:
			return DocSet.CRANFIELD;
		}
		throw new IOException("Unable to determine which document set to process.");
	}

	public String getFileName() throws IOException {
		System.out.println("  Enter data file name:");
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return in.readLine();
	}

	public String getString(String prompt) throws IOException {
		System.out.println(prompt);
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return in.readLine();
	}

	public HashMap<Integer, Document> getDocs(String filename) throws FileNotFoundException, IOException {
		String line;
		if (filename == null)
			filename = this.getFileName();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		HashMap<Integer, Document> docs = new HashMap<Integer, Document>();
		Document doc = new Document(0);
		while ((line = reader.readLine()) != null) {
			if (line.contains("*TEXT"))
			{
				int docId = Integer.parseInt(line.substring(6, 9));
				doc = new Document(docId);
				docs.put(docId, doc);
			}
			else
				doc.addText(line + " ");
		}
		for (Document doc2 : docs.values()) {
			doc2.parse();
		}
		return docs;
	}

	public HashMap<Integer, Document> getMedlarsDocs(String filename) throws FileNotFoundException, IOException {
		String line;
		if (filename == null)
			filename = this.getFileName();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		HashMap<Integer, Document> docs = new HashMap<Integer, Document>();
		Document doc = new Document(0);
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(".I "))
			{
				int docId = Integer.parseInt(line.substring(3));
				doc = new Document(docId);
				docs.put(docId, doc);
				line = reader.readLine();// Skip ".W" line
			}
			else
				doc.addText(line + " ");
		}
		for (Document doc2 : docs.values()) {
			doc2.parse();
		}
		return docs;

	}	public HashMap<Integer, Document> getCranfieldDocs(String filename) throws FileNotFoundException, IOException {
		String line;
		if (filename == null)
			filename = this.getFileName();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		HashMap<Integer, Document> docs = new HashMap<Integer, Document>();
		Document doc = new Document(0);
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(".I "))
			{
				int docId = Integer.parseInt(line.substring(3));
				doc = new Document(docId);
				docs.put(docId, doc);
				while (!line.equals(".W"))
					line = reader.readLine();// Skip forward until we hit ".W" line
			}
			else
				doc.addText(line + " ");
		}
		for (Document doc2 : docs.values()) {
			doc2.parse();
		}
		return docs;
	}

	public HashMap<String, String> getStopwords(String filename) throws FileNotFoundException, IOException {
		String stopword;
		if (filename == null)
			filename = this.getFileName();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		HashMap<String, String> stopwords = new HashMap<String, String>();
		while ((stopword = reader.readLine()) != null) {
			if (!stopword.equals(""))
				stopwords.put(stopword.toLowerCase(), stopword.toLowerCase());
		}
		return stopwords;
	}

	public int getInt(String userPrompt) throws IOException {
		System.out.println(userPrompt);
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return Integer.parseInt(in.readLine());
	}

	public double getDouble(String userPrompt) throws IOException {
		System.out.println(userPrompt);
		System.out.println("  > ");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		return Double.valueOf(in.readLine());
	}
}
