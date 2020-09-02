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


import uhh_lt.ABSA.ABSentiment.reader.InputReader;
import uhh_lt.ABSA.ABSentiment.reader.TsvReader;
import uhh_lt.ABSA.ABSentiment.reader.XMLReader;
import uhh_lt.ABSA.ABSentiment.type.Document;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Computes global IDF scores for a corpus in TSV format.
 */
public class ComputeCorpusIdfScores {


    private static DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    /**
     * Computes global IDF scores from an input file and saves them in a file; specifies a minimum frequency for terms.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the output file with term ids and idf scores
     * @param minFrequency the minimum frequency for a term
     */
    public static void computeIdfScores(String inputFile, String outputFile, int minFrequency) {
        ComputeIdf idf = new ComputeIdf();

        idf.setMinFrequency(minFrequency);
        InputReader fr;
        if (inputFile.endsWith(".xml")) {
            fr = new XMLReader(inputFile);
        } else {
            fr = new TsvReader(inputFile);
        }

        System.out.println("Computing tf-idf scores...");
        LocalDateTime start = LocalDateTime.now();
        int i = 0;
        for (Document d: fr) {
            printStatus(i, start);
            idf.addDocument(d);
            i++;
        }
        idf.saveIdfScores(outputFile);
    }

    private static void printStatus(int i, LocalDateTime start) {
        if (i>0 && i % 1000000 == 0) {
            LocalDateTime current = LocalDateTime.now();
            System.out.print(" " + current);
            long diff = getTime(start, current);
            System.out.print(" Docs/second: " + i / diff);

        }
        if (i % 50000 == 0 ) {
            System.out.print("|\n" + decimalFormat.format(i) + "\t");
        } else if (i % 10000 == 0) {
            System.out.print("|");
        } else if (i % 1000 == 0) {
            System.out.print(".");
        }
    }

    /**
     * Computes global IDF scores from an input file and saves them in a file.
     * @param inputFile file containing the input corpus
     * @param outputFile path to the output file
     */
    public static void computeIdfScores(String inputFile, String outputFile) {
        computeIdfScores(inputFile, outputFile, 1);
    }

    private static long getTime(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        return duration.getSeconds();
    }

}
