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

import uhh_lt.ABSA.ABSentiment.training.aspectclass.TrainCoarse;
import uhh_lt.ABSA.ABSentiment.training.aspecttarget.Train;

/**
 * General training class. Calls all model trainers to train on the file specified in a @link{Configuration}.
 */
public class TrainAllClassifiers {

    public static void main(String [] args) {

        uhh_lt.ABSA.ABSentiment.training.relevance.Train.main(args);
        uhh_lt.ABSA.ABSentiment.training.aspectclass.Train.main(args);
        TrainCoarse.main(args);
        uhh_lt.ABSA.ABSentiment.training.sentiment.Train.main(args);
        Train.main(args);
    }
}
