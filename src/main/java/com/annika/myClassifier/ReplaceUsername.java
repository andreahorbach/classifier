package com.annika.myClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class ReplaceUsername {
	
	public void replace(String corpusFilePathTrain, String textdateiname) {
		

	Pattern regexPattern = Pattern.compile("@([^\\s]+)");
	BufferedReader br = null;
	BufferedWriter bw = null;
	try {
		br = new BufferedReader(new FileReader(corpusFilePathTrain + textdateiname + ".txt"));
		bw = new BufferedWriter(new FileWriter(corpusFilePathTrain + textdateiname + "_neu.txt", true));
		String zeile = null;
		while ((zeile = br.readLine()) != null) {
			zeile = regexPattern.matcher(zeile).replaceAll("@Username");
			bw.write(zeile);
			bw.newLine();
		}
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		 try {
                if (bw != null)
                    bw.close();

                if (br != null)
                    br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	try {
		br = new BufferedReader(new FileReader(corpusFilePathTrain + textdateiname+ ".txt"));
		bw = new BufferedWriter(new FileWriter(corpusFilePathTrain + textdateiname + ".txt"));
		while ((br.readLine()) != null) {
			bw.write("");
		}
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		 try {
                if (bw != null)
                    bw.close();

                if (br != null)
                    br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	try {
		br = new BufferedReader(new FileReader(corpusFilePathTrain + textdateiname + "_neu.txt"));
		bw = new BufferedWriter(new FileWriter(corpusFilePathTrain + textdateiname + ".txt", true));
		String zeile = null;
		while ((zeile = br.readLine()) != null) {
			bw.write(zeile);
			bw.newLine();
		}
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		 try {
                if (bw != null)
                    bw.close();

                if (br != null)
                    br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	File file = new File(corpusFilePathTrain + textdateiname + "_neu.txt");
	file.delete();
	}
}
