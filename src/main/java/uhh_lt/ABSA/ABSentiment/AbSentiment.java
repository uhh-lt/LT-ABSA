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

package uhh_lt.ABSA.ABSentiment;

import org.apache.uima.jcas.JCas;
import uhh_lt.ABSA.ABSentiment.classifier.CrfClassifier;
import uhh_lt.ABSA.ABSentiment.classifier.LinearClassifier;
import uhh_lt.ABSA.ABSentiment.classifier.aspectclass.LinearAspectClassifier;
import uhh_lt.ABSA.ABSentiment.classifier.relevance.LinearRelevanceClassifier;
import uhh_lt.ABSA.ABSentiment.classifier.sentiment.LinearSentimentClassifer;
import uhh_lt.ABSA.ABSentiment.training.util.ProblemBuilder;
import uhh_lt.ABSA.ABSentiment.type.AspectExpression;
import uhh_lt.ABSA.ABSentiment.type.Result;
import uhh_lt.ABSA.ABSentiment.type.uima.AspectTarget;
import uhh_lt.ABSA.ABSentiment.uimahelper.Preprocessor;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
 * Abstraction class for the Aspect-based Sentiment Analysis.
 * Processes text input.
 */
public class AbSentiment extends ProblemBuilder {

    private LinearClassifier relevanceClassifier;
    private LinearClassifier aspectClassifier;
    private LinearClassifier coarseAspectClassifier;
    private LinearClassifier sentimentClassifier;
    private CrfClassifier aspectTargetClassifier;

    private Preprocessor nlpPipeline;

    /**
     * Constructor that utilizes the classifiers and the NLP pipeline.
     */
    public AbSentiment(String configurationFile) {
        initialise(configurationFile);
        if (relevanceModel != null) {
            relevanceClassifier = new LinearRelevanceClassifier(configurationFile);

        }
        aspectClassifier = new LinearAspectClassifier(configurationFile);
        coarseAspectClassifier = new LinearAspectClassifier(configurationFile);
        sentimentClassifier = new LinearSentimentClassifer(configurationFile);
        aspectTargetClassifier = new CrfClassifier(configurationFile);
        nlpPipeline = new Preprocessor(false);
    }

    /**
     * Analyzes an input string. Runs a NLP pipeline, then the classifiers.
     * @param text the input string
     * @return a structured {@link Result}
     */
    public Result analyzeText(String text) {
        nlpPipeline.processText(text);
        JCas cas = nlpPipeline.getCas();

        Result res = new Result(text);

        // extract aspect terms
        JCas aspectCas = aspectTargetClassifier.processCas(cas);
        extractAspectExpressions(aspectCas, res);

        if (relevanceClassifier != null) {
            res.setRelevance(relevanceClassifier.getLabel(cas));
            res.setRelevanceScore(relevanceClassifier.getScore());
        }

        res.setAspect(aspectClassifier.getLabel(cas));
        res.setAspectScore(aspectClassifier.getScore());

        res.setAspectCoarse(coarseAspectClassifier.getLabel(cas));
        res.setAspectCoarseScore(coarseAspectClassifier.getScore());

        res.setSentiment(sentimentClassifier.getLabel(cas));
        res.setSentimentScore(sentimentClassifier.getScore());

        return res;
    }

    /**
     * Extracts aspect expressions from a CAS.
     * @param cas the CAS containing {@link AspectTarget} annotations
     * @param res the {@link Result}, where the expressions are added
     */
    private void extractAspectExpressions(JCas cas, Result res) {
        String text = cas.getDocumentText();
        int begin = 0;
        int end = text.length()-1;

        boolean aspectActive = false;
        for (AspectTarget t: select(cas, AspectTarget.class)) {
            end = t.getEnd();
            if (aspectActive && t.getAspectTargetType().compareTo("O") == 0) {
                aspectActive = false;
                res.addAspectExpression(new AspectExpression(text.substring(begin, end), begin, end));
            } else if (!aspectActive && t.getAspectTargetType().compareTo("B") == 0) {
                aspectActive = true;
                begin = t.getBegin();
            } else if (aspectActive && t.getAspectTargetType().compareTo("B") == 0) {
                aspectActive = true;
                res.addAspectExpression(new AspectExpression(text.substring(begin, end), begin, end));
                begin = t.getBegin();
            }
        }
        if (aspectActive) {
            res.addAspectExpression(new AspectExpression(text.substring(begin, end), begin, end));
        }
    }
}
