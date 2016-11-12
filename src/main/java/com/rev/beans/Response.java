package com.rev.beans;

import java.util.List;

public class Response {

    public Software software;
    public Language language;
    public List<Match> matches;

    public static class Match {
        private String message, shortMessage;
        public Integer offset;
        public Integer length;
        public List<Replacement> replacements;
        public Context context;
        public Rule rule;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getShortMessage() {
            return shortMessage;
        }

        public void setShortMessage(String shortMessage) {
            this.shortMessage = shortMessage;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public List<Replacement> getReplacements() {
            return replacements;
        }

        public void setReplacements(List<Replacement> replacements) {
            this.replacements = replacements;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public Rule getRule() {
            return rule;
        }

        public void setRule(Rule rule) {
            this.rule = rule;
        }
    }

    public static class Language {
        private String name, code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class Software {
        private String name;
        private String version;
        private String buildDate;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBuildDate() {
            return buildDate;
        }

        public void setBuildDate(String buildDate) {
            this.buildDate = buildDate;
        }

        public String getApiVersion() {
            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        private String apiVersion;
        private String status;
    }


}
