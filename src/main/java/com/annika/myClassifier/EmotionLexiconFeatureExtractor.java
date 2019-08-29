package com.annika.myClassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS" })
public class EmotionLexiconFeatureExtractor extends FeatureExtractorResource_ImplBase implements FeatureExtractor {
	public static final String FN_ANGER_RATIO = "RatioAnger";
	public static final String FN_ANTICIPATION_RATIO = "RatioAnticipation";
	public static final String FN_DISGUST_RATIO = "RatioDisgust";
	public static final String FN_FEAR_RATIO = "RatioFear";
	public static final String FN_JOY_RATIO = "RatioJoy";
	public static final String FN_SADNESS_RATIO = "RatioSadness";
	public static final String FN_SURPRISE_RATIO = "RatioSurprise";
	public static final String FN_TRUST_RATIO = "RatioTrust";
	ArrayList<String> tweet = new ArrayList<String>();
	ArrayList<String> angerWords = new ArrayList<String>();
	ArrayList<String> anticipationWords = new ArrayList<String>();
	ArrayList<String> disgustWords = new ArrayList<String>();
	ArrayList<String> fearWords = new ArrayList<String>();
	ArrayList<String> joyWords = new ArrayList<String>();
	ArrayList<String> sadnessWords = new ArrayList<String>();
	ArrayList<String> surpriseWords = new ArrayList<String>();
	ArrayList<String> trustWords = new ArrayList<String>();

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		// hier lexicon einlesen und eine liste für jede emotion machen
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("src/main/resources/NRC-Emotion-Lexicon-Wordlevel-v0.92.txt"));
			String nextLine = null;
			while ((nextLine = br.readLine()) != null) {
				// wort emotion 0 oder 1
				String[] wordsInLine = nextLine.split("\t");
				if (wordsInLine[2].equals("1")) {
					if (wordsInLine[1].equals("anger")) {
						String word = wordsInLine[0];
						angerWords.add(wordsInLine[0]);
						System.out.println("anger " + word);
					}
					if (wordsInLine[1].equals("anticipation")) {
						String word = wordsInLine[0];
						anticipationWords.add(wordsInLine[0]);
						System.out.println("anticipation " + word);
					}
					if (wordsInLine[1].equals("disgust")) {
						String word = wordsInLine[0];
						disgustWords.add(wordsInLine[0]);
						System.out.println("disgust " +word);
					}
					if (wordsInLine[1].equals("fear")) {
						String word = wordsInLine[0];
						fearWords.add(wordsInLine[0]);
						System.out.println("fear " + word);
					}
					if (wordsInLine[1].equals("joy")) {
						String word = wordsInLine[0];
						joyWords.add(wordsInLine[0]);
						System.out.println("joy " + word);
					}
					if (wordsInLine[1].equals("sadness")) {
						String word = wordsInLine[0];
						sadnessWords.add(wordsInLine[0]);
						System.out.println("sadness " + word);
					}
					if (wordsInLine[1].equals("surprise")) {
						String word = wordsInLine[0];
						surpriseWords.add(wordsInLine[0]);
						System.out.println("surprise " + word);
					}
					if (wordsInLine[1].equals("trust")) {
						String word = wordsInLine[0];
						trustWords.add(wordsInLine[0]);
						System.out.println("trust " + word);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	// @Override
	public Set<Feature> extract(JCas jcas, TextClassificationTarget aTarget) throws TextClassificationException {
		int angerCount = 0;
		int anticipationCount = 0;
		int disgustCount = 0;
		int fearCount = 0;
		int joyCount = 0;
		int sadnessCount = 0;
		int surpriseCount = 0;
		int trustCount = 0;

		int n = 0;
		// durchläuft alle tweets
		for (POS pos : JCasUtil.selectCovered(jcas, POS.class, aTarget)) {
			// wörter zählen
			n++;
			String text = pos.getCoveredText().toLowerCase();
			// arraylist aus wörtern im tweet
			tweet.add(text);
		}
		for (int i = 0; i < tweet.size(); i++) {
			if (angerWords.contains(tweet.get(i))) {
				angerCount++;
			} else if (anticipationWords.contains(tweet.get(i))) {
				anticipationCount++;
			} else if (disgustWords.contains(tweet.get(i))) {
				disgustCount++;
			} else if (fearWords.contains(tweet.get(i))) {
				fearCount++;
			} else if (joyWords.contains(tweet.get(i))) {
				joyCount++;
			} else if (sadnessWords.contains(tweet.get(i))) {
				sadnessCount++;
			} else if (surpriseWords.contains(tweet.get(i))) {
				surpriseCount++;
			} else if (trustWords.contains(tweet.get(i))) {
				trustCount++;
			}
		}

		Set<Feature> features = new HashSet<Feature>();
		features.add(new Feature(FN_ANGER_RATIO, (double) angerCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_ANTICIPATION_RATIO, (double) anticipationCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_DISGUST_RATIO, (double) disgustCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_FEAR_RATIO, (double) fearCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_JOY_RATIO, (double) joyCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_SADNESS_RATIO, (double) sadnessCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_SURPRISE_RATIO, (double) surpriseCount / n, n == 0, FeatureType.NUMERIC));
		features.add(new Feature(FN_TRUST_RATIO, (double) trustCount / n, n == 0, FeatureType.NUMERIC));

		return features;
	}
}