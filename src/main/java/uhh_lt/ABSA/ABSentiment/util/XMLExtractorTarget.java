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

package uhh_lt.ABSA.ABSentiment.util;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import uhh_lt.ABSA.ABSentiment.featureExtractor.util.Pair;
import uhh_lt.ABSA.ABSentiment.reader.InputReader;
import uhh_lt.ABSA.ABSentiment.reader.XMLReader;
import uhh_lt.ABSA.ABSentiment.reader.XMLReaderSemEval;
import uhh_lt.ABSA.ABSentiment.type.Document;
import uhh_lt.ABSA.ABSentiment.type.Sentence;
import uhh_lt.ABSA.ABSentiment.type.uima.GoldAspectTarget;
import uhh_lt.ABSA.ABSentiment.uimahelper.Preprocessor;

import java.io.*;

/**
 * Conversion of XML format into CoNLL-type format with B-I-O Annotations for Aspect Target Expression training.
 */
public class XMLExtractorTarget {

    public static void main(String[] args) {

        String inputFile = args[0];
        String outputFile = inputFile.replace(".xml", "") + ".conll";
        if (outputFile.startsWith("/")) {
            outputFile = "." + outputFile;
        }

        Writer out = null;

        try {
            OutputStream predStream = new FileOutputStream(outputFile);
            out = new OutputStreamWriter(predStream, "UTF-8");

        } catch (FileNotFoundException | UnsupportedEncodingException e1) {
            e1.printStackTrace();
            System.exit(1);
        }


        InputReader in;
        if (args[2].compareTo("1") == 0) {
            // semeval check
            in = new XMLReaderSemEval(inputFile);
        } else {
            in =new XMLReader(inputFile);
        }

        Preprocessor preprocessor = new Preprocessor();

        for (Document d: in) {
            for (Sentence s : d.getSentences()) {
                preprocessor.processText(s.getText());
                JCas cas = preprocessor.getCas();
                for (Pair<Integer, Integer> o : s.getTargetOffsets()) {
                    GoldAspectTarget t = new GoldAspectTarget(cas, o.getFirst(), o.getSecond());
                    t.addToIndexes();
                }
                boolean inTarget = false;
                for (Token t : JCasUtil.selectCovered(cas, Token.class, 0, cas.getDocumentText().length())) {
                    try {
                        out.append(t.getCoveredText() + "\t");
                        if (JCasUtil.selectCovering(GoldAspectTarget.class, t).size() > 0) {
                            if (inTarget) {
                                out.append("I\n");
                            } else {
                                out.append("B\n");
                                inTarget = true;
                            }
                        } else {
                            out.append("O\n");
                            inTarget = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    out.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }

