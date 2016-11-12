package com.rev;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

/**
 * Created by kevin.gann on 11/12/16.
 */
public class UnescapeJsonTest {

    @Test
    public void unescapeTest(){
        String blah = "\"{\"software\":{\"name\":\"LanguageTool\",\"version\":\"3.6-SNAPSHOT\",\"buildDate\":\"2016-11-07 20:19\",\"apiVersion\":\"1\",\"status\":\"\"},\"language\":{\"name\":\"English\",\"code\":\"en\"},\"matches\":[{\"message\":\"Did you mean \"affect\"?\",\"shortMessage\":\"Commonly confused word\",\"replacements\":[{\"value\":\"affect\"}],\"offset\":43,\"length\":6,\"context\":{\"text\":\"... doctor didn't think the medicine would effect her the way that it did\",\"offset\":43,\"length\":6},\"rule\":{\"id\":\"AFFECT_EFFECT\",\"subId\":\"6\",\"description\":\"affect vs effect\",\"issueType\":\"misspelling\",\"urls\":[{\"value\":\"http://grammar.yourdictionary.com/style-and-usage/affect-effect-grammar.html\"}],\"category\":{\"id\":\"CONFUSED_WORDS\",\"name\":\"Commonly Confused Words\"}}}]}\"";
        String s = StringEscapeUtils.unescapeEcmaScript(blah);
        System.out.println(s);
    }

}
