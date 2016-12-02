package com.rev.beans;

/**
 * Created by kevin.gann on 11/12/16.
 */
public class Check {

    private String text;
    private String rule;

    public Check(String text, String rule) {
        this.text = text;
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

    public String getText() {
        return this.text;
    }

}
