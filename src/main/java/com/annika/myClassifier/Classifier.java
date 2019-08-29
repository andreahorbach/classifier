package com.annika.myClassifier;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.maxnormalization.TokenRatioPerDocument;
import org.dkpro.tc.features.ngram.CharacterNGram;
import org.dkpro.tc.features.ngram.WordNGram;
import org.dkpro.tc.features.twitter.EmoticonRatio;
import org.dkpro.tc.features.twitter.NumberOfHashTags;
import org.dkpro.tc.io.DelimiterSeparatedValuesReader;
import org.dkpro.tc.ml.builder.FeatureMode;
import org.dkpro.tc.ml.builder.LearningMode;
import org.dkpro.tc.ml.builder.MLBackend;
import org.dkpro.tc.ml.experiment.ExperimentTrainTest;
import org.dkpro.tc.ml.experiment.builder.ExperimentBuilder;
import org.dkpro.tc.ml.experiment.builder.ExperimentType;
import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.TrainTestReport;
//import org.dkpro.tc.ml.vowpalwabbit.VowpalWabbitAdapter;
//import org.dkpro.tc.ml.weka.WekaAdapter;
//import org.dkpro.tc.ml.xgboost.XgboostAdapter;
//import org.dkpro.tc.ml.liblinear.LiblinearAdapter;
//import weka.classifiers.bayes.NaiveBayes;
//import weka.classifiers.functions.LinearRegression;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
//import de.unidue.ltl.escrito.core.report.GradingEvaluationReport;
//import de.tudarmstadt.ukp.dkpro.core.arktools.ArktweetPosTagger;
//import de.tudarmstadt.ukp.dkpro.core.io.tei.TeiReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;

public class Classifier implements Constants {
	public static final String LANGUAGE_CODE = "en";

	public static final String corpusFilePathTrain = "src/main/resources/data/wassa2018/train/";
	public static final String corpusFilePathTest = "src/main/resources/data/wassa2018/dev/"; 
	public static String[] emotion = { "anger", "anticipation", "disgust", "fear", "joy", "love", "optimism",
			"pessimism", "sadness", "surprise", "trust" };
	public static int emotionPosition = 2;

	public static void main(String[] args) throws Exception {
		System.setProperty("java.util.logging.config.file", "logging.properties");
		runExperiment();
	}

	public static void runExperiment() throws Exception {
		for (int emotionPosInArray = 0; emotionPosInArray < 1; emotionPosInArray++) {
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
					DelimiterSeparatedValuesReader.class, DelimiterSeparatedValuesReader.PARAM_SOURCE_LOCATION,
					corpusFilePathTrain, DelimiterSeparatedValuesReader.PARAM_LANGUAGE, LANGUAGE_CODE,
					DelimiterSeparatedValuesReader.PARAM_TEXT_INDEX, 1,
					DelimiterSeparatedValuesReader.PARAM_OUTCOME_INDEX, emotionPosition,
					DelimiterSeparatedValuesReader.PARAM_PATTERNS, "*.txt");

			CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(
					DelimiterSeparatedValuesReader.class, DelimiterSeparatedValuesReader.PARAM_SOURCE_LOCATION,
					corpusFilePathTest, DelimiterSeparatedValuesReader.PARAM_LANGUAGE, LANGUAGE_CODE,
					DelimiterSeparatedValuesReader.PARAM_TEXT_INDEX, 1,
					DelimiterSeparatedValuesReader.PARAM_OUTCOME_INDEX, emotionPosition,
					DelimiterSeparatedValuesReader.PARAM_PATTERNS, "*.txt");

			ExperimentBuilder builder = new ExperimentBuilder();
			builder.experiment(ExperimentType.TRAIN_TEST, "trainTest-" + emotion[emotionPosInArray]) // workflow: train test
					.dataReaderTrain(readerTrain)
					.dataReaderTest(readerTest)
					.preprocessing(getPreprocessing()) // preprocessing
					.featureSets(getFeatureSet())
					.learningMode(LearningMode.SINGLE_LABEL) // classification single-label, da pro Label eigener Classifikator
					.featureMode(FeatureMode.UNIT) // in einem document mehrere Tweets, welche classifiziert werden
					.machineLearningBackend(
					 // new MLBackend(new LibsvmAdapter()),
					  new MLBackend(new LibsvmAdapter(), "-s", "3", "-c", "500"))
					.run();
			emotionPosition = emotionPosition + 1;
		}

	}

	public static TcFeatureSet getFeatureSet() {
		return new TcFeatureSet(TcFeatureFactory.create(WordNGram.class, 
				WordNGram.PARAM_NGRAM_MIN_N, "1",
				WordNGram.PARAM_NGRAM_MAX_N, "3",
				WordNGram.PARAM_NGRAM_USE_TOP_K, "10000"),
				TcFeatureFactory.create(EmotionLexiconFeatureExtractor.class));
		 //TcFeatureFactory.create(EmoticonRatio.class), wird dadurch schlechter (3 nachkommastelle)
		//TcFeatureFactory.create(NumberOfHashTags.class)); wird dadurch schlechter (3 nachkommastelle)
	}

	private static AnalysisEngineDescription getPreprocessing() throws ResourceInitializationException, IOException {
		// Username ersetzen --> verschÃ¶nern! nur einmal vorher nicht jedes mal
		//ReplaceUsername rp = new ReplaceUsername();
		//rp.replace(corpusFilePathTrain, "2018-E-c-En-train");
		//rp.replace(corpusFilePathTest, "2018-E-c-En-test");
		return createEngineDescription(createEngineDescription(BreakIteratorSegmenter.class),
				createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_LANGUAGE, "en"));

	}
}
