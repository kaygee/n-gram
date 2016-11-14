package com.rev;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.rev.beans.Check;
import com.rev.beans.Response;
import com.rev.facade.NgramFacade;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final String ENDPOINT = "https://uebkuopq30.execute-api.us-west-2.amazonaws.com/prod/languagetool/";
    private static final String CSV_INPUT = "./resources/homophones_confusion_sentences.csv";
    private static final String CSV_OUTPUT = "./resources/matches.csv";
    private static final String[] CSV_HEADER = {
            "LINE",
            "CONFUSED_WORDS",
            "AFFECT_EFFECT",
            "UPPERCASE_SENTENCE_START",
            "GRAMMAR",
            "COMPARISONS_THEN",
            "COMP_THAN",
            "CONFUSION_RULE",
            "AND_THAN",
            "DT_PRP",
            "BORED_OF",
            "TO_TOO",
            "A_PLURAL",
            "TOO_EITHER",
            "MUCH_COUNTABLE",
            "SENTENCE_FRAGMENT",
            "TOO_DETERMINER",
            "EN_CONTRACTION_SPELLING",
            "TOO_TO",
            "BEEN_PART_AGREEMENT",
            "YOUR",
            "HE_VERB_AGR",
            "A_INFINITVE",
            "YOUR_NN",
            "HAVE_PART_AGREEMENT",
            "IT_IS"
    };

    private static final int CONFUSED_WORDS_COLUMN = 1;
    private static final int AFFECT_EFFECT_COLUMN = 2;
    private static final int UPPERCASE_SENTENCE_START_COLUMN = 3;
    private static final int GRAMMAR_COLUMN = 4;
    private static final int COMPARISONS_THEN_COLUMN = 5;
    private static final int COMP_THAN_COLUMN = 6;
    private static final int CONFUSION_RULE_COLUMN = 7;
    private static final int AND_THAN_COLUMN = 8;
    private static final int DT_PRP_COLUMN = 9;
    private static final int BORED_OF_COLUMN = 10;
    private static final int TO_TOO_COLUMN = 11;
    private static final int A_PLURAL_COLUMN = 12;
    private static final int TOO_EITHER_COLUMN = 13;
    private static final int MUCH_COUNTABLE_COLUMN = 14;
    private static final int SENTENCE_FRAGMENT_COLUMN = 15;
    private static final int TOO_DETERMINER_COLUMN = 16;
    private static final int EN_CONTRACTION_SPELLING_COLUMN = 17;
    private static final int TOO_TO_COLUMN = 18;
    private static final int BEEN_PART_AGREEMENT_COLUMN = 19;
    private static final int YOUR_COLUMN = 20;
    private static final int HE_VERB_AGR_COLUMN = 21;
    private static final int A_INFINITVE_COLUMN = 22;
    private static final int YOUR_NN_COLUMN = 23;
    private static final int HAVE_PART_AGREEMENT_COLUMN = 24;
    private static final int IT_IS_COLUMN = 25;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        NgramFacade facade = new NgramFacade(ENDPOINT);

        Map<String, List<Response.Match>> matchesMap = new HashMap<String, List<Response.Match>>();

        try {
            CSVReader reader = new CSVReader(new FileReader(CSV_INPUT));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Check check = new Check();
                String line = nextLine[0];
                LOGGER.info("Check line is [" + line + "].");
                check.setText(line);
                Response response = facade.checkForMatches(check);
                List<Response.Match> matches = response.getMatches();
                if (matches.size() > 0) {
                    matchesMap.put(line, matches);
                }
            }

            writeCsvFile(matchesMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCsvFile(Map<String, List<Response.Match>> linesToMatches) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(CSV_OUTPUT), ',');
        writer.writeNext(CSV_HEADER);

        Iterator it = linesToMatches.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info(pair.getKey() + " = " + pair.getValue());
            List<Response.Match> matches = (List<Response.Match>) pair.getValue();

            Map<String, Integer> ruleToOccurrence = new HashMap();
            for (String columnHeader : CSV_HEADER) {
                ruleToOccurrence.put(columnHeader, 0);
            }

            for (Response.Match match : matches) {
                if (ruleToOccurrence.containsKey(match.getRule().getId())) {
                    ruleToOccurrence.put(match.getRule().getId(), ruleToOccurrence.get(match.getRule().getId()) + 1);
                } else {
                    ruleToOccurrence.put(match.getRule().getId(), 1);
                }
            }

            String[] csvOutputEntry = {
                    pair.getKey().toString(),
                    ruleToOccurrence.get(CSV_HEADER[CONFUSED_WORDS_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[AFFECT_EFFECT_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[UPPERCASE_SENTENCE_START_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[GRAMMAR_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[COMPARISONS_THEN_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[COMP_THAN_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[CONFUSION_RULE_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[AND_THAN_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[DT_PRP_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[BORED_OF_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[TO_TOO_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[A_PLURAL_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[TOO_EITHER_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[MUCH_COUNTABLE_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[SENTENCE_FRAGMENT_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[TOO_DETERMINER_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[EN_CONTRACTION_SPELLING_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[TOO_TO_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[BEEN_PART_AGREEMENT_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[YOUR_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[HE_VERB_AGR_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[A_INFINITVE_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[YOUR_NN_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[HAVE_PART_AGREEMENT_COLUMN]).toString(),
                    ruleToOccurrence.get(CSV_HEADER[IT_IS_COLUMN]).toString(),
            };
            writer.writeNext(csvOutputEntry);
            it.remove();
        }
        writer.close();
    }

    private static int getColumnNumber(String rule){
        for (int i = 0; i < CSV_HEADER.length; i ++){
            if (CSV_HEADER[i].equals(rule)){
                return i;
            }
        }
        return -1;
    }

}
