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

package uhh_lt.ABSA.ABSentiment.training.aspectclass;

import de.bwaldvogel.liblinear.Model;
import uhh_lt.ABSA.ABSentiment.featureExtractor.FeatureExtractor;
import uhh_lt.ABSA.ABSentiment.training.LinearTesting;
import uhh_lt.ABSA.ABSentiment.training.util.ProblemBuilder;

import java.util.Vector;

/**
 * Aspect Model Tester (fine-grained)
 */
public class Test extends ProblemBuilder {

    /**
     * Classifies an input file, given a model
     * @param args optional: input file, model file and the output file
     */
    public static void main(String[] args) {

        String modelType = "linear";
        String type = "aspect";

        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);

        loadLabelMappings(labelMappingsFileAspect);

        Vector<FeatureExtractor> features = loadFeatureExtractors(type);

        if(modelType.equals("linear")){
            LinearTesting linearTesting = new LinearTesting();
            Model model = linearTesting.loadModel(aspectModel);
            classifyTestSet(testFile, model, features, predictionFile, type, true);
        }

    }

}
