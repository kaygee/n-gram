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
    private static final String ENDPOINT = "ENDPOINT";
    private static final String CSV_INPUT = "./resources/homophones_confusion_sentences.csv";
    private static final String CSV_OUTPUT = "./resources/matches.csv";

    public static void main(String[] args) {
        BasicConfigurator.configure();
        NgramFacade facade = new NgramFacade(ENDPOINT);

        Map<String, Integer> matchesMap = new HashMap<String, Integer>();

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
                countMatches(matchesMap, line, matches);
            }

            writeCsvFile(matchesMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeCsvFile(Map<String, Integer> matchesMap) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(CSV_OUTPUT), ',');
        Iterator it = matchesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            LOGGER.info(pair.getKey() + " = " + pair.getValue());
            String[] csvOutputEntry = { pair.getKey().toString(), pair.getValue().toString() };
            writer.writeNext(csvOutputEntry);
            it.remove();
        }
        writer.close();
    }

    private static void countMatches(Map<String, Integer> matchesMap, String line, List<Response.Match> matches) {
        if (matchesMap.containsKey(line)) {
            Integer totalMatches = matchesMap.get(line);
            matchesMap.put(line, totalMatches + matches.size());
        } else {
            matchesMap.put(line, matches.size());
        }
    }
}
