package com.rev;

import com.rev.beans.Check;
import com.rev.beans.Response;
import com.rev.facade.NgramFacade;

import java.io.IOException;
import java.util.List;

public class App {

    private static final String ENDPOINT = "ENDPOINT!!!";

    private static final String TEXT = "The doctor didn't think the medicine would effect her the way that it did";

    public static void main(String[] args) {
        NgramFacade facade = new NgramFacade(ENDPOINT);
        try {
            Check check = new Check();
            check.setText(TEXT);
            Response response = facade.checkForMatches(check);
            List<Response.Match> matches = response.matches;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
