/*
 * ******************************************************************************
 *  Copyright 2017
 *  Copyright (c) 2017 Universität Hamburg
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package uhh_lt.ABSA.ABSentiment.featureExtractor.precomputation;

import uhh_lt.ABSA.ABSentiment.featureExtractor.TfIdfFeature;
import uhh_lt.ABSA.ABSentiment.reader.XMLReader;
import uhh_lt.ABSA.ABSentiment.reader.XMLReaderSemEval;
import uhh_lt.ABSA.ABSentiment.training.util.ProblemBuilder;
import uhh_lt.ABSA.ABSentiment.reader.InputReader;
import uhh_lt.ABSA.ABSentiment.reader.TsvReader;
import uhh_lt.ABSA.ABSentiment.type.Document;
import uhh_lt.ABSA.ABSentiment.type.Sentence;
import uhh_lt.ABSA.ABSentiment.uimahelper.Preprocessor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Relevance Model Trainer
 */
public class ComputeIdfTermsCategory extends ProblemBuilder {


    private static TreeMap<Double, HashMap<Integer, Integer>> termFrequencyLabel;
    private static TfIdfFeature idfScores;

    private static Preprocessor preprocessor = new Preprocessor(true);

    private static int maxEntries = 30;
    private static int minLength = 2;
    private static int minCount = 2;

    public static void computeIdfScores(String configurationFile, String inputFile, String outFile) {
        computeIdfScores(configurationFile ,inputFile, outFile, false);
    }

    public static void computeIdfScores(String configurationFile, String inputFile, String outFile, boolean useCoarseLabels) {
        computeIdfScores(configurationFile, inputFile, outFile, useCoarseLabels, null);
    }

    public static void computeIdfScores(String configurationFile,String inputFile, String outFile, boolean useCoarseLabels, String type) {
        initialise(configurationFile);
        idfScores = new TfIdfFeature(idfFile);
        termFrequencyLabel = new TreeMap<>();
        resetLabelMappings();

        InputReader in;
        if (inputFile.endsWith(".xml")){
            if (semeval16) {
                in = new XMLReaderSemEval(inputFile);
            } else {
                in = new XMLReader(inputFile);
            }
        } else {
            in = new TsvReader(inputFile);
        }

        String[] labels = null;
        HashMap<Integer, Integer> tokenCounts = null;
        for (Document doc: in) {
            for(Sentence sentence:doc.getSentences()){
                preprocessor.processText(sentence.getText());
                Collection<String> documentText = preprocessor.getTokenStrings(preprocessor.getCas());
                tokenCounts = idfScores.getTokenCounts(documentText);
                if (type == null){
                    labels = sentence.getAspectCategories();
                    if (useCoarseLabels) {
                        labels = sentence.getAspectCategoriesCoarse();
                    }
                } else if (type.compareTo("relevance") == 0) {
                    labels = sentence.getRelevance();
                } else if (type.compareTo("sentiment") == 0) {
                    try {
                        labels = sentence.getSentiment();
                    } catch (NoSuchFieldException e) {
                    }
                } else if (type.compareTo("aspect") == 0){
                    labels = sentence.getAspectCategories();
                    if (useCoarseLabels) {
                        labels = sentence.getAspectCategoriesCoarse();
                    }
                }

            }
            for (String label : labels) {
                addDocument(tokenCounts, label);
            }
        }
        saveIdfTerms(outFile);
    }

    /**
     * Setter Method for the minimum token length to be considered as candidates, default 2.
     * @param length the new minimum  token length
     */
    public void setMinLength(int length) {
        if (length >= 1){
            this.minLength = length;
        }
    }


    private static void saveIdfTerms(String fileName) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "UTF-8"));

            for (Double labelId : termFrequencyLabel.keySet()) {

                HashMap<Integer, Double> results = new HashMap<>();
                HashMap<Integer, Integer> counts = termFrequencyLabel.get(labelId);

                for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
                    double idfscore = idfScores.getIdfScore(e.getKey());
                    results.put(e.getKey(),idfscore * e.getValue());
                }

                ValueComparator bvc = new ValueComparator(results);
                TreeMap<Integer, Double> sorted_map = new TreeMap<>(bvc);
                sorted_map.putAll(results);

                int i = 0;
                String word;
                for (Map.Entry<Integer, Double> e : sorted_map.entrySet()) {

                    word = idfScores.getWordString(e.getKey());

                    if (i < maxEntries && e.getValue() > 0.5 && !word.isEmpty() && word.length() > minLength && counts.get(e.getKey())>= minCount) {
                        out.write(idfScores.getWordString(e.getKey()) + "\t" + e.getKey() + "\t" + e.getValue() +
                                "\t" +labelId + "\t" + getLabelString(labelId) +"\n");
                        i++;
                    }
                }
            }
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    private static void addDocument(HashMap<Integer, Integer> tokenCounts, String label) {
        Double labelId = getLabelId(label);
        HashMap<Integer, Integer> counts = termFrequencyLabel.get(labelId);
        if (counts == null) {
            termFrequencyLabel.put(labelId, tokenCounts);
        } else {
            for (Map.Entry<Integer, Integer> e : tokenCounts.entrySet()) {
                if (counts.get(e.getKey()) != null) {
                    counts.put(e.getKey(), e.getValue()+tokenCounts.get(e.getKey()));
                } else {
                    counts.put(e.getKey(), e.getValue());
                }
                termFrequencyLabel.put(labelId, counts);
            }
        }
    }

}

class ValueComparator implements Comparator<Integer> {
    private Map<Integer, Double> base;

    ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}