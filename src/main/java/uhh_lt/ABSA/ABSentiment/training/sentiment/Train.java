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

package uhh_lt.ABSA.ABSentiment.training.sentiment;

import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import uhh_lt.ABSA.ABSentiment.featureExtractor.FeatureExtractor;
import uhh_lt.ABSA.ABSentiment.training.util.ProblemBuilder;
import uhh_lt.ABSA.ABSentiment.training.DNNTraining;
import uhh_lt.ABSA.ABSentiment.training.LinearTraining;

import java.util.Vector;

/**
 * Sentiment Model Trainer
 */
public class Train extends ProblemBuilder {

    public static void main(String[] args) {

        String modelType = "linear";
        String type = "sentiment";
        if (args.length == 1) {
            configurationfile = args[0];
        }
        initialise(configurationfile);
        if (sentimentModel == null) {
            return;
        }

        Vector<FeatureExtractor> features = loadFeatureExtractors(type);
        Problem problem = buildProblem(trainFile, features, type, true);

        if(modelType.equals("linear")){
            LinearTraining linearTraining = new LinearTraining();
            Model model = linearTraining.trainModel(problem);
            linearTraining.saveModel(model, sentimentModel);
            saveLabelMappings(labelMappingsFileSentiment);
        }else if(modelType.equals("dnn")){
            DNNTraining dnnTraining = new DNNTraining();
            MultiLayerNetwork model = dnnTraining.trainModel(problem);
            dnnTraining.saveModel(model, sentimentModel, true);
            saveLabelMappings(labelMappingsFileSentiment);
        }
    }

}