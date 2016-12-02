package com.rev;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.rev.beans.Check;
import com.rev.beans.Response;
import com.rev.facade.NgramFacade;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final String ENDPOINT = "https://uebkuopq30.execute-api.us-west-2.amazonaws.com/prod/languagetool";
    private static final String CSV_INPUT = "./resources/rules_to_ignore.csv";
    private static final String CSV_OUTPUT = "./resources/rules_to_ignore_matches.csv";
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
            "IT_IS",
            "MASS_AGREEMENT",
            "SENT_START_CONJUNCTIVE_LINKING_ADVERB_COMMA",
            "EN_UNPAIRED_BRACKETS"
    };

    public static void main(String[] args) {
        BasicConfigurator.configure();
        LogManager.getLogger("org.apache").setLevel(Level.INFO);
        LogManager.getLogger("com.rev").setLevel(Level.INFO);
        NgramFacade facade = new NgramFacade(ENDPOINT);

        Map<String, List<Response.Match>> matchesMap = new HashMap<String, List<Response.Match>>();
        int lineCount = 0;

        try {
            CSVReader reader = new CSVReader(new FileReader(CSV_INPUT));
            String[] nextLine;
            lineCount ++;
            int noMatchCount = 0;
            while ((nextLine = reader.readNext()) != null) {
                Check check = new Check(nextLine[0], nextLine[1]);
                LOGGER.info("Rule [" + check.getRule() + "] is the text [" + check.getText() + "].");
                Response response = facade.checkForMatches(check);
                List<Response.Match> matches = response.getMatches();
                if (matches.size() > 0) {
                    matchesMap.put(check.getText(), matches);
                } else {
                    LOGGER.info("Check line [" + check.getText() + "] has no matches.");
                    noMatchCount ++;
                }
            }

            LOGGER.info("Number of queries with no matches returned [" + noMatchCount + "].");
            LOGGER.info("Number of queries with matches returned [" + matchesMap.size() + "].");
            LOGGER.info("Number lines [" + lineCount + "].");

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
            List<Response.Match> matches = (List<Response.Match>) pair.getValue();
            Map<String, Integer> ruleToOccurrence = new HashMap();

            for (Response.Match match : matches) {
                if (ruleToOccurrence.containsKey(match.getRule().getId())) {
                    ruleToOccurrence.put(match.getRule().getId(), ruleToOccurrence.get(match.getRule().getId()) + 1);
                } else {
                    ruleToOccurrence.put(match.getRule().getId(), 1);
                }
            }

            String[] outputLine = getOutputLine(pair.getKey().toString(), ruleToOccurrence);
            writer.writeNext(outputLine);
            it.remove();
        }
        writer.close();
    }

    private static String[] getOutputLine(String line, Map<String, Integer> ruleToOccurrence) {
        String[] csvOutputEntry = new String[CSV_HEADER.length];
        Arrays.fill(csvOutputEntry, "0");

        csvOutputEntry[0] = line;

        Iterator it = ruleToOccurrence.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String rule = (String) pair.getKey();
            int columnNumber = getColumnNumber(rule);
            if (columnNumber == -1){
                LOGGER.info("Couldn't find the rule [" + rule + "] in the header list!");
                System.exit(1);
            }
            csvOutputEntry[columnNumber] = ruleToOccurrence.get(rule).toString();
        }
        return csvOutputEntry;
    }

    private static int getColumnNumber(String rule) {
        for (int i = 0; i < CSV_HEADER.length; i++) {
            if (CSV_HEADER[i].equals(rule)) {
                return i;
            }
        }
        return -1;
    }

}
